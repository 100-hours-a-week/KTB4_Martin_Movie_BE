package com.homework4.workapi.serviceTest.Post;

import com.homework4.workapi.dto.post.request.PostRequest;
import com.homework4.workapi.dto.post.response.PostResponse;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostAddServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserService userService;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("게시글 생성 success - 게시글을 저장한다")
    void addPost_success() {
        Long userId = 1L;

        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", userId);

        PostRequest request = new PostRequest("테스트 제목", "테스트 내용", 8);

        when(userService.findUserById(userId)).thenReturn(user);
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostResponse response = postService.addPost(userId, request);

        assertNotNull(response);
        assertEquals("테스트 제목", response.getTitle());
        assertEquals("테스트 내용", response.getContent());
        assertEquals(8, response.getRating());
        assertEquals(userId, response.getUserId());

        verify(postRepository).save(argThat(post ->
                post.getRating() == 8
        ));
    }
}
