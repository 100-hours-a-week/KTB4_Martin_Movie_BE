package com.homework4.workapi.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.homework4.workapi.document.PostSearchDocument;
import com.homework4.workapi.dto.post.response.PostListResponse;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.projection.CommentCountProjection;
import com.homework4.workapi.repository.CommentRepository;
import com.homework4.workapi.repository.PostLikeRepository;
import com.homework4.workapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostSearchService {

    private static final int POST_PAGE_SIZE = 10;

    private final ElasticsearchOperations elasticsearchOperations;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

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

        List<Post> orderedPosts = findPostsInSearchOrder(
                searchPage.getContent()
        );

        Page<Post> postPage = new PageImpl<>(
                orderedPosts,
                searchPage.getPageable(),
                searchPage.getTotalElements()
        );

        return toPostListResponse(
                postPage,
                userId
        );
    }

    private void validatePage(int page) {
        if (page < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "페이지는 1 이상이어야 합니다."
            );
        }
    }

    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "검색어를 입력해야 합니다."
            );
        }
    }

    private Page<Long> searchPostIds(
            String keyword,
            int page
    ) {
        Pageable pageable = PageRequest.of(
                page - 1,
                POST_PAGE_SIZE
        );

        NativeQuery query = NativeQuery.builder()
                .withQuery(queryBuilder ->
                        queryBuilder.multiMatch(multiMatch ->
                                multiMatch
                                        .query(keyword.trim())
                                        .fields(
                                                "title^2",
                                                "content"
                                        )
                                        .type(TextQueryType.CrossFields)
                                        .operator(Operator.And)
                        )
                )
                .withPageable(pageable)
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

    private List<Post> findPostsInSearchOrder(
            List<Long> postIds
    ) {
        if (postIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Post> postMap =
                postRepository.findAllByIdsWithDetails(postIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Post::getId,
                                post -> post
                        ));

        return postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private Page<PostListResponse> toPostListResponse(
            Page<Post> postPage,
            Long userId
    ) {
        List<Long> postIds = postPage.getContent()
                .stream()
                .map(Post::getId)
                .toList();

        Map<Long, Integer> commentCountMap =
                getCommentCountMap(postIds);

        Set<Long> likedPostIds =
                getLikedPostIds(
                        userId,
                        postIds
                );

        return postPage.map(post ->
                PostListResponse.from(
                        post,
                        commentCountMap.getOrDefault(
                                post.getId(),
                                0
                        ),
                        likedPostIds.contains(
                                post.getId()
                        )
                )
        );
    }

    private Map<Long, Integer> getCommentCountMap(
            List<Long> postIds
    ) {
        if (postIds.isEmpty()) {
            return Map.of();
        }

        return commentRepository.countByPostIds(postIds)
                .stream()
                .collect(Collectors.toMap(
                        CommentCountProjection::getPostId,
                        result -> Math.toIntExact(
                                result.getCommentCount()
                        )
                ));
    }

    private Set<Long> getLikedPostIds(
            Long userId,
            List<Long> postIds
    ) {
        if (postIds.isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(
                postLikeRepository.findLikedPostIds(
                        userId,
                        postIds
                )
        );
    }
}