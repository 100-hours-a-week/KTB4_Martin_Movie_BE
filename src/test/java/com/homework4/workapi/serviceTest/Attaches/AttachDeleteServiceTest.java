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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachDeleteServiceTest {

    @Mock
    AttachRepository attachRepository;

    @Mock
    PostService postService;

    @Mock
    FileService fileService;

    @InjectMocks
    AttachService attachService;

    @Test
    @DisplayName("첨부 삭제 success - 작성자가 파일과 DB 데이터를 삭제한다")
    void deleteAttach_success() {
        Long userId = 1L;
        Long postId = 10L;
        Long attachId = 100L;

        User writer = createUser(userId);
        Post post = createPost(postId, writer);

        Attach attach = createAttach(attachId, post, "/images/test.png");

        when(attachRepository.findById(attachId)).thenReturn(Optional.of(attach));

        AttachResponse response = attachService.deleteAttach(attachId, userId);

        assertEquals(attachId, response.id());
        assertEquals(postId, response.postId());

        assertEquals("/images/test.png", response.attachUrl());

        verify(attachRepository).findById(attachId);

        verify(fileService).deleteImage("/images/test.png");

        verify(attachRepository).delete(attach);

        verifyNoInteractions(postService);
    }

    @Test
    @DisplayName("첨부 삭제 fail - 첨부가 없으면 404가 발생한다")
    void deleteAttach_fail_notFound() {
        Long attachId = 999L;
        Long userId = 1L;

        when(attachRepository.findById(attachId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                        () -> attachService.deleteAttach(attachId, userId)
                );

        assertEquals(404, exception.getStatusCode().value());

        verify(attachRepository).findById(attachId);

        verifyNoInteractions(fileService);

        verify(attachRepository, never()).delete(any(Attach.class));
    }

    @Test
    @DisplayName("첨부 삭제 fail - 작성자가 아니면 403이 발생한다")
    void deleteAttach_fail_notWriter() {
        Long writerId = 1L;
        Long otherUserId = 2L;
        Long attachId = 100L;

        User writer = createUser(writerId);
        Post post = createPost(10L, writer);

        Attach attach = createAttach(attachId, post, "/images/test.png");

        when(attachRepository.findById(attachId)).thenReturn(Optional.of(attach));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                        () -> attachService.deleteAttach(attachId, otherUserId)
                );

        assertEquals(403, exception.getStatusCode().value());

        verify(attachRepository).findById(attachId);

        verifyNoInteractions(fileService);

        verify(attachRepository, never()).delete(any(Attach.class));
    }

    @Test
    @DisplayName("첨부 삭제 fail - 실제 파일 삭제 실패 시 DB는 삭제하지 않는다")
    void deleteAttach_fail_fileDelete() {
        Long userId = 1L;
        Long attachId = 100L;

        User writer = createUser(userId);
        Post post = createPost(10L, writer);

        Attach attach = createAttach(attachId, post, "/images/test.png");

        when(attachRepository.findById(attachId)).thenReturn(Optional.of(attach));

        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.")
        ).when(fileService)
                .deleteImage("/images/test.png");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                        () -> attachService.deleteAttach(attachId, userId)
                );

        assertEquals(500, exception.getStatusCode().value());

        verify(fileService).deleteImage("/images/test.png");

        verify(attachRepository, never()).delete(any(Attach.class));
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
