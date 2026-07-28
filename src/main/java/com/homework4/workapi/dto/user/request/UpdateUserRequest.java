package com.homework4.workapi.dto.user.request;

import com.homework4.workapi.validation.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @NotBlank(message = "사용자 이름은 필수 입니다.")
    @Size(
            max = ValidationConstants.USERNAME_MAX_LENGTH,
            message = "닉네임은 최대 10자까지 입력할 수 있습니다."
    )
    String username
) {
}
