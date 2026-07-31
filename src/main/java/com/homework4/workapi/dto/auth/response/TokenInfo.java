package com.homework4.workapi.dto.auth.response;

public record TokenInfo(
        String accessToken,
        long expiresIn
) {
}
