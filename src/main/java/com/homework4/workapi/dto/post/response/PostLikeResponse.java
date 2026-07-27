package com.homework4.workapi.dto.post.response;

import lombok.Getter;

@Getter
public class PostLikeResponse{
    private int likeCount;
    private boolean liked;

    public PostLikeResponse(int likeCount, boolean liked) {
        this.likeCount = likeCount;
        this.liked = liked;
    }
}


