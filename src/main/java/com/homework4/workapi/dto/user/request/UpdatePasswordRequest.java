package com.homework4.workapi.dto.user.request;

import com.homework4.workapi.validation.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, message = "비밀번호의 최소 길이는 8자 입니다.") // 길이가 8 미만
    @Size(max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "비밀번호의 최대 길이는 20자 입니다.") // 길이가 20 초과
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "INVALID_FORMAT"
    ) // 영문, 숫자, 특수문자를 최소 1개씩 포함해야 함
    String newPassword
) {
}
