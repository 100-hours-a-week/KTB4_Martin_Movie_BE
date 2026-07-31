package com.homework4.workapi.dto.common;

public record CommonResponse<T>(
        String message,
        T data
) {
    public static <T> CommonResponse<T> of(String message, T data) {
        return new CommonResponse<>(message, data);
    }
}
