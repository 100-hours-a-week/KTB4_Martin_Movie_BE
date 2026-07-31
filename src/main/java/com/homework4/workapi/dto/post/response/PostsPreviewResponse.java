package com.homework4.workapi.dto.post.response;

import com.homework4.workapi.entity.Post;

import java.time.LocalDateTime;

public record PostsPreviewResponse(
        Long id,
        String title,
        String content,
        String username,
        LocalDateTime createTime,
        int commentCount,
        String thumbnail,
        Long viewCount,
        int rating,
        String profileImageUrl
) {
    public static PostsPreviewResponse from(Post post, int commentCount) {
        return new PostsPreviewResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getDisplayUsername(),
                post.getCreateTime(),
                commentCount,
                post.getAttaches().isEmpty()
                        ? null
                        : post.getAttaches().get(0).getAttachUrl(),
                post.getViewCount(),
                post.getRating(),
                post.getUser().getDisplayProfileImageUrl()
        );
    }
}
