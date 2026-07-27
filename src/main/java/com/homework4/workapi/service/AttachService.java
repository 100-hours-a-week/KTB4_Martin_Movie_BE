package com.homework4.workapi.service;

import com.homework4.workapi.dto.attach.response.AttachResponse;
import com.homework4.workapi.entity.Attach;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.repository.AttachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachService {
    private final AttachRepository attachRepository;
    private final PostService postService;
    private final FileService fileService;

    @Transactional
    public AttachResponse addAttach(Long postId, Long userId, UUID uploadKey, MultipartFile file) {
        Post post = postService.findPostById(postId);

        if(!post.isWritten(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 첨부파일을 첨부 할 수 있습니다.");
        }
        Optional<Attach> existingAttach =
                attachRepository.findByPost_IdAndUploadKey(postId, uploadKey.toString());

        if (existingAttach.isPresent()) {
            return new AttachResponse(existingAttach.get());
        }

        String attachUrl = fileService.saveImage(file);
        Attach attach = new Attach(post, attachUrl, uploadKey.toString());

        Attach savedAttach = attachRepository.save(attach);

        return new AttachResponse(savedAttach);
    }

    public List<AttachResponse> getAttaches(Long postId) {
        postService.findPostById(postId);

        List<Attach> attaches = attachRepository.findByPost_Id(postId);
        List<AttachResponse> responses = new ArrayList<>();

        for (Attach attach : attaches) {
            responses.add(new AttachResponse(attach));
        }

        return responses;
    }

    @Transactional
    public AttachResponse deleteAttach(Long attachId,  Long userId) {
        Optional<Attach> Attach = attachRepository.findById(attachId);

        if (Attach.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "첨부파일을 찾을 수 없습니다.");
        }


        Attach attach = Attach.get();
        Post post = attach.getPost();
        if(!post.isWritten(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 삭제 할 수 있습니다.");
        }

        fileService.deleteImage(attach.getAttachUrl());
        attachRepository.delete(attach);

        return new AttachResponse(attach);
    }
}