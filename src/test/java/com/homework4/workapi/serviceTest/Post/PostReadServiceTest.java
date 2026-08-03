package com.homework4.workapi.serviceTest.Post;

import com.homework4.workapi.dto.post.response.PostListResponse;
import com.homework4.workapi.dto.post.response.PostResponse;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.PostLike;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.projection.CommentCountProjection;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostReadServiceTest {

    @Mock PostRepository postRepository;
    @Mock UserService userService;
    @Mock PostLikeRepository postLikeRepository;
    @Mock CommentRepository commentRepository;

    @InjectMocks PostService postService;

    @Test
    @DisplayName("게시글 목록 조회 success - 여러 게시글 Page 조회")
    void getPosts_success() {
        User user = user(1L, "kim");
        Post post1 = post(10L, user, "제목1", "내용1", 7);
        Post post2 = post(20L, user, "제목2", "내용2", 7);

        CommentCountProjection countResult = mock(CommentCountProjection.class);
        when(countResult.getPostId()).thenReturn(10L);
        when(countResult.getCommentCount()).thenReturn(2L);

        when(postRepository.findAllWithUser(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post1, post2)));
        when(commentRepository.countByPostIds(
                List.of(10L, 20L)
        )).thenReturn(List.of(countResult));
        Long userId = 1L;

        when(postLikeRepository.findLikedPostIds(
                userId,
                List.of(10L, 20L)
        )).thenReturn(List.of(10L));

        Page<PostListResponse> responses =
                postService.getPosts(userId, 1);

        assertEquals(2, responses.getTotalElements());
        assertEquals(2, responses.getContent().get(0).commentCount());
        assertEquals(0, responses.getContent().get(1).commentCount());
        assertTrue(responses.getContent().get(0).liked());
        assertFalse(responses.getContent().get(1).liked());

        verify(postRepository).findAllWithUser(any(Pageable.class));
        verify(commentRepository).countByPostIds(List.of(10L, 20L));
        verify(commentRepository, never()).countByPost_Id(anyLong());
    }

    @Test
    @DisplayName("게시글 단건 조회 success - 좋아요 안 누른 상태")
    void getPost_success_notLiked() {
        Long postId = 10L;
        Long userId = 1L;

        User writer = user(2L, "writer");
        Post post = post(postId, writer, "제목", "내용", 5);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.countByPost_Id(postId)).thenReturn(3);
        when(postLikeRepository.findByPost_IdAndUser_Id(postId, userId)).thenReturn(Optional.empty());

        PostResponse response = postService.getPost(postId, userId);

        assertEquals(postId, response.id());
        assertEquals("제목", response.title());
        assertEquals(3, response.commentCount());
        assertFalse(response.liked());
    }

    @Test
    @DisplayName("게시글 단건 조회 success - 좋아요 누른 상태")
    void getPost_success_liked() {
        Long postId = 10L;
        Long userId = 1L;

        User user = user(userId, "kim");
        Post post = post(postId, user, "제목", "내용", 6);
        PostLike postLike = new PostLike(post, user);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.countByPost_Id(postId)).thenReturn(1);
        when(postLikeRepository.findByPost_IdAndUser_Id(postId, userId)).thenReturn(Optional.of(postLike));

        PostResponse response = postService.getPost(postId, userId);

        assertTrue(response.liked());
        assertEquals(1, response.commentCount());
    }

    @Test
    @DisplayName("게시글 단건 조회 fail - 게시글이 없으면 실패한다")
    void getPost_fail_notFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.getPost(999L, 1L)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(commentRepository, never()).countByPost_Id(anyLong());
        verify(postLikeRepository, never()).findByPost_IdAndUser_Id(anyLong(), anyLong());
    }

    private User user(Long id, String username) {
        User user = new User(username, username + "@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Post post(Long id, User user, String title, String content, int rating) {
        Post post = new Post(user, title, content, rating);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }
}
