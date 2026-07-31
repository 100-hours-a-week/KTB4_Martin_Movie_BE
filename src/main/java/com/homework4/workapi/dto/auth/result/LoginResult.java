package com.homework4.workapi.dto.auth.result;

import com.homework4.workapi.dto.auth.response.LoginResponse;

public record LoginResult(
        LoginResponse response,
        String refreshToken
) {
}
