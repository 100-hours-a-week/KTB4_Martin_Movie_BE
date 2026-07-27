package com.homework4.workapi.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
    @NotBlank(message = "사용자 이름은 필수 입니다.")
    String username
) {
}
