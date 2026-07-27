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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentUpdateServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostService postService;

    @Mock
    UserService userService;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글 수정 success - 작성자면 댓글을 수정할 수 있다")
    void updateComment_success() {
        Long postId = 10L;
        Long commentId = 100L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post post = createPost(postId, user);
        Comment comment = createComment(commentId, user, post, "기존 댓글");

        CommentRequest request = new CommentRequest("수정 댓글");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.updateComment(commentId, request, postId, userId);

        assertEquals(commentId, response.getId());
        assertEquals(postId, response.getPostId());
        assertEquals(userId, response.getUserId());
        assertEquals("kim", response.getUsername());
        assertEquals("수정 댓글", response.getContent());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 수정 fail - 댓글이 존재하지 않으면 실패한다")
    void updateComment_fail_commentNotFound() {
        Long postId = 10L;
        Long commentId = 999L;
        Long userId = 1L;

        CommentRequest request = new CommentRequest("수정 댓글");

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.updateComment(commentId, request, postId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 수정 fail - 댓글이 해당 게시글의 댓글이 아니면 실패한다")
    void updateComment_fail_postMismatch() {
        Long realPostId = 10L;
        Long requestPostId = 999L;
        Long commentId = 100L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post realPost = createPost(realPostId, user);
        Comment comment = createComment(commentId, user, realPost, "기존 댓글");

        CommentRequest request = new CommentRequest("수정 댓글");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.updateComment(commentId, request, requestPostId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 수정 fail - 작성자가 아니면 실패한다")
    void updateComment_fail_notWriter() {
        Long postId = 10L;
        Long commentId = 100L;
        Long writerId = 1L;
        Long requestUserId = 999L;

        User writer = createUser(writerId, "kim");
        Post post = createPost(postId, writer);
        Comment comment = createComment(commentId, writer, post, "기존 댓글");

        CommentRequest request = new CommentRequest("수정 댓글");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.updateComment(commentId, request, postId, requestUserId)
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    private User createUser(Long id, String username) {
        User user = new User(username, username + "@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Post createPost(Long id, User user) {
        Post post = new Post(user, "게시글 제목", "게시글 내용", 7);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    private Comment createComment(Long id, User user, Post post, String content) {
        Comment comment = new Comment(user, post, content);
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }
}
