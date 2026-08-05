package com.homework4.workapi.serviceTest.Post;

import com.homework4.workapi.dto.post.response.PostResponse;
import com.homework4.workapi.entity.Attach;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.event.PostSearchSyncEvent;
import com.homework4.workapi.repository.CommentRepository;
import com.homework4.workapi.repository.PostLikeRepository;
import com.homework4.workapi.repository.PostRepository;
import com.homework4.workapi.service.FileService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostDeleteServiceTest {

    @Mock
    FileService fileService;

    @Mock
    PostRepository postRepository;

    @Mock
    UserService userService;

    @Mock
    PostLikeRepository postLikeRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("게시글 삭제 success - 작성자면 게시글을 삭제할 수 있다")
    void deletePost_success() {
        Long postId = 10L;
        Long userId = 1L;

        User writer = createUser(userId, "kim");
        Post post = createPost(postId, writer, "제목", "내용");

        post.getAttaches().add(new Attach(post, "/images/first.png", UUID.randomUUID().toString()));
        post.getAttaches().add(new Attach(post, "/images/second.jpg", UUID.randomUUID().toString()));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostResponse response = postService.deletePost(postId, userId);

        assertEquals(postId, response.id());
        assertEquals("제목", response.title());
        assertEquals("내용", response.content());
        assertEquals(userId, response.userId());
        assertEquals("kim", response.username());


        verify(postRepository, times(1)).findById(postId);
        verify(fileService).deleteImage("/images/first.png");
        verify(fileService).deleteImage("/images/second.jpg");
        verify(postRepository, times(1)).delete(post);
        verify(eventPublisher).publishEvent(
                PostSearchSyncEvent.delete(postId)
        );
    }

    @Test
    @DisplayName("게시글 삭제 fail - 게시글이 존재하지 않으면 삭제에 실패한다")
    void deletePost_fail_postNotFound() {
        Long postId = 999L;
        Long userId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.deletePost(postId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("게시글 삭제 fail - 작성자가 아니면 삭제에 실패한다")
    void deletePost_fail_notWriter() {
        Long postId = 10L;
        Long writerId = 1L;
        Long requestUserId = 999L;

        User writer = createUser(writerId, "kim");
        Post post = createPost(postId, writer, "제목", "내용");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.deletePost(postId, requestUserId)
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any(Post.class));
    }

    private User createUser(Long id, String username) {
        User user = new User(username, username + "@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Post createPost(Long id, User user, String title, String content) {
        Post post = new Post(user, title, content, 8);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }
}
