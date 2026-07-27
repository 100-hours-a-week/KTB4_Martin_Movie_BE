package com.homework4.workapi.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfo {

    private String accessToken;
    private long expiresIn;
}