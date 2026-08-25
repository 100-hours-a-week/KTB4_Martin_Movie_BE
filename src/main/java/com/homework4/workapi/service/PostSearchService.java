package com.homework4.workapi.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.homework4.workapi.document.PostSearchDocument;
import com.homework4.workapi.dto.post.response.PostListResponse;
import com.homework4.workapi.event.PostSearchSyncEvent;
import com.homework4.workapi.projection.PostListProjection;
import com.homework4.workapi.repository.PostRepository;
import com.homework4.workapi.repository.PostSearchRepository;
import static com.homework4.workapi.common.PaginationConstants.POST_PAGE_SIZE;
import static com.homework4.workapi.validation.ValidationConstants.MIN_PAGE;
import static com.homework4.workapi.validation.ValidationConstants.PAGE_MIN_MESSAGE;
import static com.homework4.workapi.validation.ValidationConstants.MAX_SEARCH_PAGE;
import static com.homework4.workapi.validation.ValidationConstants.PAGE_MAX_MESSAGE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final PostRepository postRepository;
    private final PostService postService;
    private final PostSearchRepository postSearchRepository;

    public Page<PostListResponse> searchPosts(
            Long userId,
            int page,
            String keyword
    ) {
        validatePage(page);
        validateKeyword(keyword);

        Page<Long> searchPage = searchPostIds(
                keyword,
                page
        );

        List<PostListProjection> orderedPosts =
                findPostsInSearchOrder(
                        searchPage.getContent()
                );

        List<PostListResponse> responses =
                postService.toPostListResponses(
                        orderedPosts,
                        userId
                );

        return new PageImpl<>(
                responses,
                searchPage.getPageable(),
                searchPage.getTotalElements()
        );
    }

    private void validatePage(int page) {
        if (page < MIN_PAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PAGE_MIN_MESSAGE);
        }
        if (page > MAX_SEARCH_PAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PAGE_MAX_MESSAGE);
        }
    }

    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색어를 입력해야 합니다.");
        }
    }

    private Page<Long> searchPostIds(
            String keyword,
            int page
    ) {
        Pageable pageable = PageRequest.of(page - MIN_PAGE, POST_PAGE_SIZE);

        NativeQuery query = NativeQuery.builder()
                .withQuery(queryBuilder ->
                        queryBuilder.bool(boolQuery ->
                                boolQuery
                                        .should(shouldQuery ->
                                                shouldQuery.multiMatch(multiMatch ->
                                                        multiMatch
                                                                .query(keyword.trim())
                                                                .fields(
                                                                        "title^2",
                                                                        "content"
                                                                )
                                                                .type(TextQueryType.CrossFields)
                                                                .operator(Operator.And)
                                                                .boost(3.0f)
                                                )
                                        )
                                        .should(shouldQuery ->
                                                shouldQuery.match(match ->
                                                        match
                                                                .field("title")
                                                                .query(keyword.trim())
                                                                .operator(Operator.And)
                                                                .fuzziness("AUTO")
                                                                .maxExpansions(20)
                                                                .boost(1.0f)
                                                )
                                        )
                                        .minimumShouldMatch("1")
                        )
                )
                .withPageable(pageable)
                .withSourceFilter(FetchSourceFilter.of(false, null, null))
                .withTrackTotalHits(true)
                .withSort(sort ->
                        sort.score(score ->
                                score.order(SortOrder.Desc)
                        )
                )
                .withSort(sort ->
                        sort.field(field ->
                                field.field("createTime")
                                        .order(SortOrder.Desc)
                        )
                )
                .withSort(sort ->
                        sort.field(field ->
                                field.field("id")
                                        .order(SortOrder.Desc)
                        )
                )
                .build();

        SearchHits<PostSearchDocument> searchHits =
                elasticsearchOperations.search(
                        query,
                        PostSearchDocument.class
                );

        List<Long> postIds = searchHits.getSearchHits()
                .stream()
                .map(searchHit ->
                        Long.valueOf(searchHit.getId())
                )
                .toList();

        return new PageImpl<>(
                postIds,
                pageable,
                searchHits.getTotalHits()
        );
    }

    private List<PostListProjection> findPostsInSearchOrder(
            List<Long> postIds
    ) {
        if (postIds.isEmpty()) {
            return List.of();
        }

        Map<Long, PostListProjection> postMap =
                postRepository.findAllPostListByIds(postIds)
                        .stream()
                        .collect(Collectors.toMap(
                                PostListProjection::getId,
                                post -> post
                        ));

        return postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            readOnly = true
    )
    public void syncPostSearchIndex(PostSearchSyncEvent event) {
        try {
            if (event.type() == PostSearchSyncEvent.Type.DELETE) {
                postSearchRepository.deleteById(event.postId());
                return;
            }

            postRepository.findById(event.postId())
                    .ifPresent(post ->
                            postSearchRepository.save(
                                    PostSearchDocument.from(post)
                            )
                    );
        } catch (Exception exception) {
            log.error(
                    "게시글 검색 색인 동기화에 실패했습니다. postId={}",
                    event.postId(),
                    exception
            );
        }
    }
}
