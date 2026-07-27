package com.homework4.workapi.dto.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommonResponse<T> {
    private final String message;
    private final T data;
    public static <T> CommonResponse<T> of(String message, T data) {
        return new CommonResponse<>(message, data);
    }

}
