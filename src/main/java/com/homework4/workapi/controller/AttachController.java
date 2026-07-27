package com.homework4.workapi.controller;

import com.homework4.workapi.dto.attach.response.AttachResponse;
import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.service.AttachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class AttachController {

    @Autowired
    private AttachService attachService;

    @PostMapping(
            value = "/posts/{postId}/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public CommonResponse<AttachResponse> addAttach(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @RequestHeader("Idempotency-Key") UUID uploadKey,
            @RequestParam("file") MultipartFile file
    ) {
        AttachResponse attachResponse = attachService.addAttach(postId, userId, uploadKey, file);
        return new CommonResponse<>("첨부파일이 등록되었습니다.", attachResponse);
    }

    @GetMapping("/posts/{postId}/attachments")
    public CommonResponse<List<AttachResponse>> getAttaches(
            @PathVariable Long postId
    ) {
        List<AttachResponse> attaches = attachService.getAttaches(postId);
        return new CommonResponse<>(null, attaches);
    }

    @DeleteMapping("/attachments/{attachId}")
    public CommonResponse<AttachResponse> deleteAttach(
            @PathVariable Long attachId,
            @AuthenticationPrincipal Long userId
    ) {
        AttachResponse attachResponse = attachService.deleteAttach(attachId, userId);
        return new CommonResponse<>("첨부파일이 삭제되었습니다.", attachResponse);
    }
}