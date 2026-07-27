package com.homework4.workapi.serviceTest.Comment;

import com.homework4.workapi.dto.comment.request.CommentRequest;
import com.homework4.workapi.dto.comment.response.CommentResponse;
import com.homework4.workapi.entity.Comment;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.repository.CommentRepository;
import com.homework4.workapi.service.CommentService;
import com.homework4.workapi.service.PostService;
import com.homework4.workapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentAddServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostService postService;

    @Mock
    UserService userService;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글 생성 success - 댓글 생성에 성공한다")
    void addComment_success() {
        Long userId = 1L;
        Long postId = 10L;

        User user = createUser(userId, "kim");
        Post post = createPost(postId, user);

        CommentRequest request = new CommentRequest("댓글 내용");

        when(userService.findUserById(userId)).thenReturn(user);
        when(postService.findPostById(postId)).thenReturn(post);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    ReflectionTestUtils.setField(comment, "id", 100L);
                    return comment;
                });

        CommentResponse response = commentService.addComment(request, postId, userId);

        assertEquals(100L, response.getId());
        assertEquals(postId, response.getPostId());
        assertEquals(userId, response.getUserId());
        assertEquals("kim", response.getUsername());
        assertEquals("댓글 내용", response.getContent());

        verify(userService).findUserById(userId);
        verify(postService).findPostById(postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 fail - 사용자가 존재하지 않으면 실패한다")
    void addComment_fail_userNotFound() {
        Long userId = 999L;
        Long postId = 10L;

        CommentRequest request = new CommentRequest("댓글 내용");

        when(userService.findUserById(userId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(request, postId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(userService).findUserById(userId);
        verify(postService, never()).findPostById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 fail - 게시글이 존재하지 않으면 실패한다")
    void addComment_fail_postNotFound() {
        Long userId = 1L;
        Long postId = 999L;

        User user = createUser(userId, "kim");

        CommentRequest request = new CommentRequest("댓글 내용");

        when(userService.findUserById(userId)).thenReturn(user);
        when(postService.findPostById(postId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(request, postId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(userService).findUserById(userId);
        verify(postService).findPostById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    private User createUser(Long id, String username) {
        User user = new User(username, username + "@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Post createPost(Long id, User user) {
        Post post = new Post(user, "게시글 제목", "게시글 내용", 8);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }
}
