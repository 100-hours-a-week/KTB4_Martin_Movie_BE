package com.homework4.workapi.dto.post.response;

import com.homework4.workapi.dto.attach.response.AttachResponse;
import com.homework4.workapi.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String username;
    private int likeCount;
    private int commentCount;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private List<AttachResponse> attaches;
    private boolean liked;
    private long viewCount;
    private int rating;
    private String profileImageUrl;

    public PostResponse(Post post, int commentCount, boolean liked) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getUser().getDisplayUsername();
        this.likeCount = post.getLikeCount();
        this.commentCount = commentCount;
        this.liked =  liked;
        this.updateTime = post.getUpdateTime();
        this.createTime = post.getCreateTime();
        this.attaches = post.getAttaches().stream()
                .map(AttachResponse::new)
                .toList();
        this.viewCount = post.getViewCount();
        this.rating = post.getRating();
        this.profileImageUrl = post.getUser().getDisplayProfileImageUrl();
    }

}
