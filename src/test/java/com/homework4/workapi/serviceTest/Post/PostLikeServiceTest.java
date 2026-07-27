package com.homework4.workapi.serviceTest.Post;

import com.homework4.workapi.dto.post.response.PostLikeResponse;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.PostLike;
import com.homework4.workapi.entity.User;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserService userService;

    @Mock
    PostLikeRepository postLikeRepository;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("좋아요 success - 아직 좋아요를 누르지 않았다면 좋아요가 증가한다")
    void likePost_success_newLike() {
        Long postId = 10L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post post = createPost(postId, user, "제목", "내용");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserById(userId)).thenReturn(user);
        when(postLikeRepository.findByPost_IdAndUser_Id(postId, userId))
                .thenReturn(Optional.empty());

        PostLikeResponse response = postService.likePost(postId, userId);

        assertEquals(1, response.getLikeCount());
        assertTrue(response.isLiked());

        verify(postRepository, times(1)).findById(postId);
        verify(userService, times(1)).findUserById(userId);
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 success - 이미 좋아요를 눌렀다면 중복으로 증가하지 않는다")
    void likePost_success_alreadyLiked() {
        Long postId = 10L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post post = createPost(postId, user, "제목", "내용");
        post.likeIncrease();

        PostLike postLike = new PostLike(post, user);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserById(userId)).thenReturn(user);
        when(postLikeRepository.findByPost_IdAndUser_Id(postId, userId))
                .thenReturn(Optional.of(postLike));

        PostLikeResponse response = postService.likePost(postId, userId);

        assertEquals(1, response.getLikeCount());
        assertTrue(response.isLiked());

        verify(postLikeRepository, never()).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 fail - 게시글이 존재하지 않으면 실패한다")
    void likePost_fail_postNotFound() {
        Long postId = 999L;
        Long userId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.likePost(postId, userId)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(postRepository, times(1)).findById(postId);
        verify(userService, never()).findUserById(anyLong());
        verify(postLikeRepository, never()).findByPost_IdAndUser_Id(anyLong(), anyLong());
        verify(postLikeRepository, never()).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 취소 success - 좋아요를 누른 상태면 좋아요가 취소된다")
    void unlikePost_success_liked() {
        Long postId = 10L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post post = createPost(postId, user, "제목", "내용");
        post.likeIncrease();

        PostLike postLike = new PostLike(post, user);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserById(userId)).thenReturn(user);
        when(postLikeRepository.findByPost_IdAndUser_Id(postId, userId))
                .thenReturn(Optional.of(postLike));

        PostLikeResponse response = postService.unlikePost(postId, userId);

        assertEquals(0, response.getLikeCount());
        assertFalse(response.isLiked());

        verify(postLikeRepository, times(1)).delete(postLike);
    }

    @Test
    @DisplayName("좋아요 취소 success - 좋아요를 누르지 않은 상태면 그대로 유지된다")
    void unlikePost_success_notLiked() {
        Long postId = 10L;
        Long userId = 1L;

        User user = createUser(userId, "kim");
        Post post = createPost(postId, user, "제목", "내용");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.findUserById(userId)).thenReturn(user);
        when(postLikeRepository.findByPost_IdAndUser_Id(postId, userId))
                .thenReturn(Optional.empty());

        PostLikeResponse response = postService.unlikePost(postId, userId);

        assertEquals(0, response.getLikeCount());
        assertFalse(response.isLiked());

        verify(postLikeRepository, never()).delete(any(PostLike.class));
    }

    private User createUser(Long id, String username) {
        User user = new User(username, username + "@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Post createPost(Long id, User user, String title, String content) {
        Post post = new Post(user, title, content, 7);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }
}
