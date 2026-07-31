package com.homework4.workapi.dto.comment.response;

import com.homework4.workapi.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        Long userId,
        String username,
        String content,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        String profileImageUrl
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getUser().getDisplayUsername(),
                comment.getContent(),
                comment.getCreateTime(),
                comment.getUpdateTime(),
                comment.getUser().getDisplayProfileImageUrl()
        );
    }
}
