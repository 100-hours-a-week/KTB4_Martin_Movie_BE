package com.homework4.workapi.serviceTest.Post;

import com.homework4.workapi.document.PostSearchDocument;
import com.homework4.workapi.dto.post.response.PostListResponse;
import com.homework4.workapi.repository.PostRepository;
import com.homework4.workapi.repository.PostSearchRepository;
import com.homework4.workapi.service.PostSearchService;
import com.homework4.workapi.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostSearchServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostService postService;

    @Mock
    private PostSearchRepository postSearchRepository;

    @Mock
    private SearchHits<PostSearchDocument> searchHits;

    @InjectMocks
    private PostSearchService postSearchService;

    @Test
    void searchPosts_rejectsPagesOutsideTheAllowedRangeBeforeSearching() {
        assertBadRequest(0);
        assertBadRequest(-1);
        assertBadRequest(1001);

        verifyNoInteractions(
                elasticsearchOperations,
                postRepository,
                postService,
                postSearchRepository
        );
    }

    @Test
    void searchPosts_allowsFirstAndLastSearchPage() {
        when(searchHits.getSearchHits()).thenReturn(List.of());
        when(searchHits.getTotalHits()).thenReturn(0L);
        when(elasticsearchOperations.search(
                any(NativeQuery.class),
                eq(PostSearchDocument.class)
        )).thenReturn(searchHits);

        Page<PostListResponse> firstPage =
                postSearchService.searchPosts(1L, 1, "keyword");
        Page<PostListResponse> lastPage =
                postSearchService.searchPosts(1L, 1000, "keyword");

        assertEquals(0, firstPage.getPageable().getPageNumber());
        assertEquals(999, lastPage.getPageable().getPageNumber());
        verify(elasticsearchOperations, times(2)).search(
                any(NativeQuery.class),
                eq(PostSearchDocument.class)
        );
    }

    private void assertBadRequest(int page) {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postSearchService.searchPosts(1L, page, "keyword")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
