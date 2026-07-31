package com.homework4.workapi.dto.auth.response;

import com.homework4.workapi.dto.user.response.UserResponse;

public record LoginResponse(
        UserResponse user,
        TokenInfo token
) {
    public static LoginResponse of(
            UserResponse user,
            String accessToken,
            long expiresIn
    ) {
        return new LoginResponse(
                user,
                new TokenInfo(accessToken, expiresIn)
        );
    }
}
