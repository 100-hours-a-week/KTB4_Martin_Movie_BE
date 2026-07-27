package com.homework4.workapi.dto.attach.response;

import com.homework4.workapi.entity.Attach;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class AttachResponse {
    private Long id;
    private Long postId;
    private String attachUrl;
    private LocalDateTime attachTime;

    public AttachResponse(Attach attach) {
        this.id = attach.getId();
        this.postId = attach.getPost().getId();
        this.attachUrl = attach.getAttachUrl();
        this.attachTime = attach.getAttachTime();
    }
}
