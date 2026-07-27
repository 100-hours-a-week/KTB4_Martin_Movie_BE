package com.homework4.workapi.exception;

import org.springframework.http.HttpStatus;

public class AuthorizedException extends BusinessException {
    public AuthorizedException(String code) {
        super(code, HttpStatus.UNAUTHORIZED);
    }
}