package com.homework4.workapi.dto.user.response;

import com.homework4.workapi.entity.User;

public record UserResponse(
        Long id,
        String username,
        String email,
        String profileImageUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayUsername(),
                user.isDeleted()
                        ? null
                        : user.getEmail(),
                user.getDisplayProfileImageUrl()
        );
    }
}
