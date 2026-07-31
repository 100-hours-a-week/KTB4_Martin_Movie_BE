package com.homework4.workapi.dto.attach.response;

import com.homework4.workapi.entity.Attach;

import java.time.LocalDateTime;

public record AttachResponse(
        Long id,
        Long postId,
        String attachUrl,
        LocalDateTime attachTime
) {
    public static AttachResponse from(Attach attach) {
        return new AttachResponse(
                attach.getId(),
                attach.getPost().getId(),
                attach.getAttachUrl(),
                attach.getAttachTime()
        );
    }
}
