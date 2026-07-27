package com.homework4.workapi.serviceTest.Comment;

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
class CommentDeleteServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostService postService;

    @Mock
    UserService userService;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글 삭제 success - 작성자면 댓글을 삭제할 수 있다")
    void deleteComment_success() {
        Long postId = 10L;
        Long commentId = 100L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post post = createPost(postId, user);
        Comment comment = createComment(commentId, user, post, "댓글 내용");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(postId, commentId, userId);

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 fail - 댓글이 존재하지 않으면 실패한다")
    void deleteComment_fail_commentNotFound() {
        Long postId = 10L;
        Long commentId = 999L;
        Long userId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.deleteComment(postId, commentId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 fail - 댓글이 해당 게시글의 댓글이 아니면 실패한다")
    void deleteComment_fail_postMismatch() {
        Long realPostId = 10L;
        Long requestPostId = 999L;
        Long commentId = 100L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post realPost = createPost(realPostId, user);
        Comment comment = createComment(commentId, user, realPost, "댓글 내용");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.deleteComment(requestPostId, commentId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 fail - 작성자가 아니면 실패한다")
    void deleteComment_fail_notWriter() {
        Long postId = 10L;
        Long commentId = 100L;
        Long writerId = 1L;
        Long requestUserId = 999L;

        User writer = createUser(writerId, "kim");
        Post post = createPost(postId, writer);
        Comment comment = createComment(commentId, writer, post, "댓글 내용");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.deleteComment(postId, commentId, requestUserId)
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).delete(any(Comment.class));
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

    private Comment createComment(Long id, User user, Post post, String content) {
        Comment comment = new Comment(user, post, content);
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }
}
