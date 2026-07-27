package com.homework4.workapi.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {
    // 에러를 식별하기 위한 코드 값을 받아 예외를 생성
    public NotFoundException(String code) {
        // 부모 클래스인 BusinessException에
        // 에러 코드와 HTTP 404(Not Found) 상태를 함께 전달
        super(code, HttpStatus.NOT_FOUND);
    }
}
