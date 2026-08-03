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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        when(commentRepository.findByPost_Id(eq(postId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(comment1, comment2)));

        Page<CommentResponse> responses = commentService.getComments(postId, 1);

        assertEquals(2, responses.getTotalElements());

        assertEquals(100L, responses.getContent().get(0).id());
        assertEquals(postId, responses.getContent().get(0).postId());
        assertEquals(2L, responses.getContent().get(0).userId());
        assertEquals("kim", responses.getContent().get(0).username());
        assertEquals("댓글1", responses.getContent().get(0).content());

        assertEquals(101L, responses.getContent().get(1).id());
        assertEquals("댓글2", responses.getContent().get(1).content());

        verify(postService, times(1)).findPostById(postId);
        verify(commentRepository, times(1))
                .findByPost_Id(eq(postId), any(Pageable.class));
    }

    @Test
    @DisplayName("댓글 목록 조회 success - 댓글이 없으면 빈 리스트를 반환한다")
    void getComments_success_emptyList() {
        Long postId = 10L;

        User writer = createUser(1L, "writer");
        Post post = createPost(postId, writer);

        when(postService.findPostById(postId)).thenReturn(post);
        when(commentRepository.findByPost_Id(eq(postId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<CommentResponse> responses = commentService.getComments(postId, 1);

        assertEquals(0, responses.getTotalElements());
        assertEquals(0, responses.getContent().size());

        verify(postService, times(1)).findPostById(postId);
        verify(commentRepository, times(1))
                .findByPost_Id(eq(postId), any(Pageable.class));
    }

    @Test
    @DisplayName("댓글 목록 조회 fail - 게시글이 존재하지 않으면 실패한다")
    void getComments_fail_postNotFound() {
        Long postId = 999L;

        when(postService.findPostById(postId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(postId, 1)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(postService, times(1)).findPostById(postId);
        verify(commentRepository, never())
                .findByPost_Id(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("댓글 목록 조회 fail - 페이지가 1보다 작으면 실패한다")
    void getComments_fail_invalidPage() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(10L, 0)
        );

        assertEquals(400, exception.getStatusCode().value());
        verifyNoInteractions(postService, commentRepository);
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
