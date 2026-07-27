package com.homework4.workapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    // 클라이언트 및 내부 분기 처리를 위한 에러 코드
    private final String code;

    // HTTP 응답에 사용할 상태 코드
    private final HttpStatus status;

    // 에러 코드와 HTTP 상태 코드를 함께 받아 예외를 생성
    public BusinessException(String code, HttpStatus status) {
        // RuntimeException의 메시지로 에러 코드를 전달
        super(code);
        this.code = code;
        this.status = status;
    }
}