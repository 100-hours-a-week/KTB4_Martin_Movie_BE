package com.homework4.workapi.serviceTest.Post;

import com.homework4.workapi.dto.post.response.PostResponse;
import com.homework4.workapi.dto.post.request.UpdatePostRequest;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.event.PostSearchSyncEvent;
import com.homework4.workapi.repository.CommentRepository;
import com.homework4.workapi.repository.PostLikeRepository;
import com.homework4.workapi.repository.PostRepository;
import com.homework4.workapi.service.PostService;
import com.homework4.workapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostUpdateServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("게시글 수정 success - 제목, 내용, 별점을 수정한다")
    void updatePost_success() {
        Long postId = 10L;
        Long userId = 1L;

        User writer = createUser(userId, "kim");
        Post post = createPost(postId, writer, "기존 제목", "기존 내용", 4);

        UpdatePostRequest request = new UpdatePostRequest("수정 제목", "수정 내용", 9);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.countByPost_Id(postId)).thenReturn(2);

        PostResponse response = postService.updatePost(postId, userId, request);

        assertEquals("수정 제목", post.getTitle());
        assertEquals("수정 내용", post.getContent());
        assertEquals(9, post.getRating());
        assertEquals(9, response.rating());
        assertEquals(2, response.commentCount());
        verify(eventPublisher).publishEvent(
                PostSearchSyncEvent.upsert(postId)
        );
    }
    @Test
    @DisplayName("게시글 수정 fail - 게시글이 존재하지 않으면 수정에 실패한다")
    void updatePost_fail_postNotFound() {
        Long postId = 999L;
        Long userId = 1L;

        UpdatePostRequest request = new UpdatePostRequest("수정 제목", "수정 내용", 9);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.updatePost(postId, userId, request)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, never()).countByPost_Id(anyLong());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 수정 fail - 작성자가 아니면 수정에 실패한다")
    void updatePost_fail_notWriter() {
        Long postId = 10L;
        Long writerId = 1L;
        Long requestUserId = 999L;

        User writer = createUser(writerId, "kim");
        Post post = createPost(postId, writer, "기존 제목", "기존 내용", 4);

        UpdatePostRequest request = new UpdatePostRequest("수정 제목", "수정 내용", 9);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.updatePost(postId, requestUserId, request)
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, never()).countByPost_Id(anyLong());
        verify(postRepository, never()).save(any(Post.class));
    }

    private User createUser(Long id, String username) {
        User user = new User(username, username + "@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Post createPost(Long id, User user, String title, String content, int rating) {
        Post post = new Post(user, title, content, rating);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }
}
