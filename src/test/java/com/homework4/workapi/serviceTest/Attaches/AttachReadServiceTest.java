package com.homework4.workapi.serviceTest.Attaches;

import com.homework4.workapi.dto.attach.response.AttachResponse;
import com.homework4.workapi.entity.Attach;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.repository.AttachRepository;
import com.homework4.workapi.service.AttachService;
import com.homework4.workapi.service.FileService;
import com.homework4.workapi.service.PostService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachReadServiceTest {

    @Mock
    AttachRepository attachRepository;

    @Mock
    PostService postService;

    @Mock
    FileService fileService;

    @InjectMocks
    AttachService attachService;

    @Test
    @DisplayName("첨부 조회 success - 게시글의 첨부 목록을 반환한다")
    void getAttaches_success() {
        Long postId = 10L;

        User writer = createUser(1L);
        Post post = createPost(postId, writer);

        Attach firstAttach = createAttach(100L, post, "/images/first.png");

        Attach secondAttach = createAttach(200L, post, "/images/second.png");

        when(postService.findPostById(postId)).thenReturn(post);

        when(attachRepository.findByPost_Id(postId)).thenReturn(List.of(firstAttach, secondAttach));

        List<AttachResponse> responses = attachService.getAttaches(postId);

        assertEquals(2, responses.size());

        assertEquals(100L, responses.get(0).getId());

        assertEquals("/images/first.png", responses.get(0).getAttachUrl());

        assertEquals(200L, responses.get(1).getId());

        assertEquals("/images/second.png", responses.get(1).getAttachUrl());

        verify(postService).findPostById(postId);
        verify(attachRepository).findByPost_Id(postId);
        verifyNoInteractions(fileService);
    }

    @Test
    @DisplayName("첨부 조회 success - 첨부가 없으면 빈 목록을 반환한다")
    void getAttaches_success_empty() {
        Long postId = 10L;
        Post post = createPost(postId, createUser(1L));

        when(postService.findPostById(postId)).thenReturn(post);

        when(attachRepository.findByPost_Id(postId)).thenReturn(List.of());

        List<AttachResponse> responses = attachService.getAttaches(postId);

        assertTrue(responses.isEmpty());

        verify(postService).findPostById(postId);
        verify(attachRepository).findByPost_Id(postId);
    }

    @Test
    @DisplayName("첨부 조회 fail - 게시글이 없으면 404가 발생한다")
    void getAttaches_fail_postNotFound() {
        Long postId = 999L;

        when(postService.findPostById(postId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> attachService.getAttaches(postId)
                );

        assertEquals(404, exception.getStatusCode().value());

        verify(postService).findPostById(postId);

        verify(attachRepository, never()).findByPost_Id(anyLong());

        verifyNoInteractions(fileService);
    }

    private User createUser(Long userId) {
        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    private Post createPost(Long postId, User writer) {
        Post post = new Post(writer, "영화 제목", "영화 리뷰", 8);
        ReflectionTestUtils.setField(post, "id", postId);
        return post;
    }

    private Attach createAttach(Long attachId, Post post, String attachUrl) {
        Attach attach = new Attach(post, attachUrl, UUID.randomUUID().toString());
        ReflectionTestUtils.setField(attach, "id", attachId);
        return attach;
    }
}
