package com.homework4.workapi.dto.comment.response;

import com.homework4.workapi.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String profileImageUrl;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.userId = comment.getUser().getId();
        this.username = comment.getUser().getDisplayUsername();
        this.content = comment.getContent();
        this.createTime = comment.getCreateTime();
        this.updateTime = comment.getUpdateTime();
        this.profileImageUrl = comment.getUser().getDisplayProfileImageUrl();
    }
}
