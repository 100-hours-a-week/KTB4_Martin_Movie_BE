package com.homework4.workapi.dto.user.response;

import com.homework4.workapi.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getDisplayUsername();
        this.email = user.isDeleted()
                ? null
                : user.getEmail();
        this.profileImageUrl = user.getDisplayProfileImageUrl();
    }
}
