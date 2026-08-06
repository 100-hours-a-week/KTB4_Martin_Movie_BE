package com.homework4.workapi.dto.post.response;

import com.homework4.workapi.projection.PostListProjection;

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
    public static PostListResponse from(PostListProjection post, int commentCount, boolean liked, String thumbnailUrl) {
        String username = post.isDeleted() ? "(알 수 없음)" : post.getUsername();
        String profileImageUrl = post.isDeleted() ? null : post.getProfileImageUrl();

        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                username,
                post.getLikeCount(),
                commentCount,
                post.getCreateTime(),
                thumbnailUrl,
                liked,
                post.getViewCount(),
                post.getRating(),
                profileImageUrl
        );
    }
}