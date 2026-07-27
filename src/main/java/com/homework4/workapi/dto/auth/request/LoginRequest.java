package com.homework4.workapi.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "REQUIRED")
    @Email(message = "INVALID_FORMAT")
    String email,

    @NotBlank(message = "REQUIRED")
    String password
) {
}
