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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachAddServiceTest {

    @Mock
    AttachRepository attachRepository;
    @Mock
    PostService postService;
    @Mock
    FileService fileService;
    @InjectMocks
    AttachService attachService;
    @Test
    @DisplayName("첨부 성공 success: 첨부 파일 등록이 성공한다.")
    void addattach_success() {
        Long userId = 1L;
        Long postId = 1L;
        UUID uploadKey = UUID.randomUUID();

        User writer = createUser(userId);
        Post post = createPost (postId, writer);

        MockMultipartFile file = new MockMultipartFile("file", "poster.png", "image/png", "image-data".getBytes());

        when(postService.findPostById(postId)).thenReturn(post);
        when(fileService.saveImage(file)).thenReturn("/images/test.png");
        when(attachRepository.saveAndFlush(any(Attach.class)))
                .thenAnswer(invocation -> {
                    Attach attach = invocation.getArgument(0);

                    ReflectionTestUtils.setField(attach, "id", 100L);

                    return attach;
                });
        when(attachRepository.findByPost_IdAndUploadKey(postId, uploadKey.toString()))
                .thenReturn(Optional.empty());

        AttachResponse response = attachService.addAttach(postId, userId, uploadKey, file);
        assertEquals(100L, response.id());
        assertEquals(postId, response.postId());

        assertEquals("/images/test.png", response.attachUrl());

        verify(postService).findPostById(postId);
        verify(fileService).saveImage(file);
        verify(attachRepository).saveAndFlush(any(Attach.class));
    }


    @Test
    @DisplayName("첨부 등록 fail - 작성자가 아니면 403이 발생한다")
    void addAttach_fail_notWriter() {
        Long postId = 10L;
        Long writerId = 1L;
        Long otherUserId = 2L;
        UUID uploadKey = UUID.randomUUID();

        User writer = createUser(writerId);
        Post post = createPost(postId, writer);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "poster.png",
                "image/png",
                "image-data".getBytes()
        );

        when(postService.findPostById(postId)).thenReturn(post);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachService.addAttach(
                        postId,
                        otherUserId,
                        uploadKey,
                        file
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(postService).findPostById(postId);
        verifyNoInteractions(fileService);
        verifyNoInteractions(attachRepository);
    }

    @Test
    @DisplayName("같은 uploadKey 재요청 시 기존 첨부를 반환한다")
    void addAttach_duplicate_returnsExistingAttach() {
        Long postId = 1L;
        Long userId = 1L;
        UUID uploadKey = UUID.randomUUID();

        Post post = createPost(postId, createUser(userId));
        Attach existing = new Attach(post, "/images/test.png", uploadKey.toString());
        ReflectionTestUtils.setField(existing, "id", 100L);

        when(postService.findPostById(postId)).thenReturn(post);
        when(attachRepository.findByPost_IdAndUploadKey(postId, uploadKey.toString()))
                .thenReturn(Optional.of(existing));

        AttachResponse response = attachService.addAttach(
                postId, userId, uploadKey,
                new MockMultipartFile("file", "poster.png", "image/png", new byte[1])
        );

        assertEquals(100L, response.id());
        verifyNoInteractions(fileService);
        verify(attachRepository, never()).save(any());
    }

    @Test
    @DisplayName(
            "첨부 등록 conflict - 저장 중 uploadKey가 중복되면 S3 파일을 삭제하고 409를 반환한다"
    )
    void addAttach_conflict_duplicateDuringSave() {
        Long postId = 1L;
        Long userId = 1L;
        UUID uploadKey = UUID.randomUUID();

        User writer = createUser(userId);

        Post post = createPost(postId, writer);

        MockMultipartFile file = new MockMultipartFile("file", "poster.png", "image/png", "image-data".getBytes());

        String uploadedUrl = "/images/duplicate.png";

        when(postService.findPostById(postId)).thenReturn(post);

        when(attachRepository.findByPost_IdAndUploadKey(postId, uploadKey.toString())).thenReturn(Optional.empty());

        when(fileService.saveImage(file)).thenReturn(uploadedUrl);

        when(attachRepository.saveAndFlush(any(Attach.class))).thenThrow(
                new DataIntegrityViolationException("duplicate upload key"));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> attachService.addAttach(
                                postId,
                                userId,
                                uploadKey,
                                file
                        )
                );

        assertEquals(409, exception.getStatusCode().value());

        verify(fileService).deleteImage(uploadedUrl);

        verify(attachRepository).saveAndFlush(any(Attach.class));
    }


    private User createUser(Long userId) {
        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    private Post createPost(Long postId, User writer) {
        Post post = new Post(writer, "영화제목", "영화리뷰", 8);
        ReflectionTestUtils.setField(post, "id", postId);
        return post;
    }

}
