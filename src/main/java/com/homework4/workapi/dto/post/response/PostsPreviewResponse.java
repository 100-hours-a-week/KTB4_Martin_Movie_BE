package com.homework4.workapi.dto.post.response;

import com.homework4.workapi.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostsPreviewResponse {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createTime;
    private int commentCount;
    private String thumbnail;
    private Long viewCount;
    private int rating;
    private String profileImageUrl;

    public PostsPreviewResponse(Post post, int commentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getUser().getDisplayUsername();
        this.commentCount = commentCount;
        this.createTime = post.getCreateTime();
        this.viewCount = post.getViewCount();
        this.thumbnail = post.getAttaches().isEmpty()
                ? null
                : post.getAttaches().get(0).getAttachUrl();
        this.rating = post.getRating();
        this.profileImageUrl = post.getUser().getDisplayProfileImageUrl();
    }
}
