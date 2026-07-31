package com.homework4.workapi.dto.auth.result;

import com.homework4.workapi.dto.auth.response.TokenInfo;

public record TokenResult(
        TokenInfo token,
        String newRefreshToken
) {
}
