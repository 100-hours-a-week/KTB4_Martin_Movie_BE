package com.homework4.workapi.serviceTest.Comment;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentReadServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostService postService;

    @Mock
    UserService userService;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글 목록 조회 success - 게시글의 댓글 목록을 조회한다")
    void getComments_success() {
        Long postId = 10L;

        User writer = createUser(1L, "writer");
        User commenter = createUser(2L, "kim");
        Post post = createPost(postId, writer);

        Comment comment1 = createComment(100L, commenter, post, "댓글1");
        Comment comment2 = createComment(101L, commenter, post, "댓글2");

        when(postService.findPostById(postId)).thenReturn(post);
        when(commentRepository.findByPostIdWithUser(postId))
                .thenReturn(List.of(comment1, comment2));

        List<CommentResponse> responses = commentService.getComments(postId);

        assertEquals(2, responses.size());

        assertEquals(100L, responses.get(0).id());
        assertEquals(postId, responses.get(0).postId());
        assertEquals(2L, responses.get(0).userId());
        assertEquals("kim", responses.get(0).username());
        assertEquals("댓글1", responses.get(0).content());

        assertEquals(101L, responses.get(1).id());
        assertEquals("댓글2", responses.get(1).content());

        verify(postService, times(1)).findPostById(postId);
        verify(commentRepository, times(1)).findByPostIdWithUser(postId);
    }

    @Test
    @DisplayName("댓글 목록 조회 success - 댓글이 없으면 빈 리스트를 반환한다")
    void getComments_success_emptyList() {
        Long postId = 10L;

        User writer = createUser(1L, "writer");
        Post post = createPost(postId, writer);

        when(postService.findPostById(postId)).thenReturn(post);
        when(commentRepository.findByPostIdWithUser(postId))
                .thenReturn(List.of());

        List<CommentResponse> responses = commentService.getComments(postId);

        assertEquals(0, responses.size());

        verify(postService, times(1)).findPostById(postId);
        verify(commentRepository, times(1)).findByPostIdWithUser(postId);
    }

    @Test
    @DisplayName("댓글 목록 조회 fail - 게시글이 존재하지 않으면 실패한다")
    void getComments_fail_postNotFound() {
        Long postId = 999L;

        when(postService.findPostById(postId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(postId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(postService, times(1)).findPostById(postId);
        verify(commentRepository, never()).findByPostIdWithUser(anyLong());
    }

    private User createUser(Long id, String username) {
        User user = new User(username, username + "@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Post createPost(Long id, User user) {
        Post post = new Post(user, "게시글 제목", "게시글 내용", 5);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    private Comment createComment(Long id, User user, Post post, String content) {
        Comment comment = new Comment(user, post, content);
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }
}
