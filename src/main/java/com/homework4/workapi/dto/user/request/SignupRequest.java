package com.homework4.workapi.dto.user.request;

import com.homework4.workapi.validation.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record SignupRequest(
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(
            max = ValidationConstants.USERNAME_MAX_LENGTH,
            message = "닉네임은 최대 10자까지 입력할 수 있습니다."
    )
    String username,

    @NotBlank(message = "이메일은 필수 입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(
            min = ValidationConstants.PASSWORD_MIN_LENGTH,
            max = ValidationConstants.PASSWORD_MAX_LENGTH,
            message = "비밀번호는 8자 이상 20자 이하여야 합니다."
    )
    @Pattern(
            regexp = ValidationConstants.PASSWORD_PATTERN,
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    String password,

    MultipartFile profileImage
) {
}
