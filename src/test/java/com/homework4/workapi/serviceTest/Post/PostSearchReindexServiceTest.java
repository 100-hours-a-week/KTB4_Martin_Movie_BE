package com.homework4.workapi.serviceTest.Post;

import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.repository.PostRepository;
import com.homework4.workapi.repository.PostSearchRepository;
import com.homework4.workapi.service.PostSearchReindexService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostSearchReindexServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostSearchRepository postSearchRepository;

    @InjectMocks
    private PostSearchReindexService reindexService;

    @Test
    void reindexAfter_indexesOneBatchAndReportsContinuation() {
        Post firstPost = createPost(10L, "첫 번째 글");
        Post secondPost = createPost(20L, "두 번째 글");

        when(postRepository.findByIdGreaterThanOrderByIdAsc(any(Long.class), any(Pageable.class)))
                .thenReturn(List.of(firstPost, secondPost));

        PostSearchReindexService.ReindexBatch result =
                reindexService.reindexAfter(0L, 1);

        assertEquals(10L, result.lastPostId());
        assertEquals(1, result.indexedCount());
        assertTrue(result.hasNext());
        verify(postSearchRepository).saveAll(any());
    }

    @Test
    void reindexAfter_returnsCompletionWhenNoPostsRemain() {
        when(postRepository.findByIdGreaterThanOrderByIdAsc(any(Long.class), any(Pageable.class)))
                .thenReturn(List.of());

        PostSearchReindexService.ReindexBatch result =
                reindexService.reindexAfter(20L, 500);

        assertEquals(20L, result.lastPostId());
        assertEquals(0, result.indexedCount());
        assertFalse(result.hasNext());
    }

    private Post createPost(Long id, String title) {
        User user = new User(
                "kim",
                "kim" + id + "@test.com",
                "encodedPassword",
                null
        );
        Post post = new Post(user, title, "내용", 5);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }
}
