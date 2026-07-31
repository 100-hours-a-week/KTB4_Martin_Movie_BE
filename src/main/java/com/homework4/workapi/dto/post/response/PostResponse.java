package com.homework4.workapi.dto.post.response;

import com.homework4.workapi.dto.attach.response.AttachResponse;
import com.homework4.workapi.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long userId,
        String username,
        int likeCount,
        int commentCount,
        LocalDateTime updateTime,
        LocalDateTime createTime,
        List<AttachResponse> attaches,
        boolean liked,
        long viewCount,
        int rating,
        String profileImageUrl
) {
    public PostResponse {
        attaches = List.copyOf(attaches);
    }

    public static PostResponse from(Post post, int commentCount, boolean liked) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getId(),
                post.getUser().getDisplayUsername(),
                post.getLikeCount(),
                commentCount,
                post.getUpdateTime(),
                post.getCreateTime(),
                post.getAttaches().stream()
                        .map(AttachResponse::from)
                        .toList(),
                liked,
                post.getViewCount(),
                post.getRating(),
                post.getUser().getDisplayProfileImageUrl()
        );
    }
}
