package com.homework4.workapi.dto.post.response;

public record PostLikeResponse(
        int likeCount,
        boolean liked
) {
}
