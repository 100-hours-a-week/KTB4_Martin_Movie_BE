package com.homework4.workapi.projection;

import java.time.LocalDateTime;

public interface PostListProjection {

    Long getId();
    String getTitle();
    String getContent();
    String getUsername();
    boolean isDeleted();
    String getProfileImageUrl();
    int getLikeCount();
    LocalDateTime getCreateTime();
    long getViewCount();
    int getRating();
}