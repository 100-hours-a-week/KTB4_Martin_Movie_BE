package com.homework4.workapi.dto.post.response;

import com.homework4.workapi.entity.Post;

import java.time.LocalDateTime;

public record PostListResponse(
        Long id,
        String title,
        String content,
        String username,
        int likeCount,
        int commentCount,
        LocalDateTime createTime,
        String thumbnailUrl,
        boolean liked,
        long viewCount,
        int rating,
        String profileImageUrl
) {
    public static PostListResponse from(Post post, int commentCount, boolean liked) {
        String thumbnailUrl = post.getAttaches().stream()
                .findFirst()
                .map(attach -> attach.getAttachUrl())
                .orElse(null);

        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getDisplayUsername(),
                post.getLikeCount(),
                commentCount,
                post.getCreateTime(),
                thumbnailUrl,
                liked,
                post.getViewCount(),
                post.getRating(),
                post.getUser().getDisplayProfileImageUrl()
        );
    }
}