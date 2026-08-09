package com.homework4.workapi.dtoTest.User;

import com.homework4.workapi.dto.auth.request.LoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRequestTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("로그인 요청 fail - email이 빈칸이면 검증에 실패한다")
    void loginRequest_fail_blankEmail() {
        LoginRequest request = createRequest("", "Test1234!");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "email");
    }

    @Test
    @DisplayName("로그인 요청 fail - password가 빈칸이면 검증에 실패한다")
    void loginRequest_fail_blankPassword() {
        LoginRequest request = createRequest("kim@test.com", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password");
    }

    private LoginRequest createRequest(String email, String password) {
        return new LoginRequest(email, password);
    }

    private void assertHasViolation(
            Set<ConstraintViolation<LoginRequest>> violations,
            String fieldName
    ) {
        assertTrue(
                violations.stream()
                        .anyMatch(violation ->
                                violation.getPropertyPath().toString().equals(fieldName)
                        )
        );
    }
}
