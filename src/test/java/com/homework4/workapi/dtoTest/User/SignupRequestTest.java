package com.homework4.workapi.dtoTest.User;

import com.homework4.workapi.dto.user.request.SignupRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SignupRequestTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("회원가입 요청 success - 올바른 값이면 검증에 성공한다")
    void signupRequest_success() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "Test1234!", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("회원가입 요청 fail - username이 빈칸이면 검증에 실패한다")
    void signupRequest_fail_blankUsername() {
        SignupRequest request = new SignupRequest("", "kim@test.com", "Test1234!", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "username");
    }

    @Test
    @DisplayName("회원가입 요청 fail - email이 빈칸이면 검증에 실패한다")
    void signupRequest_fail_blankEmail() {
        SignupRequest request = new SignupRequest("kim", "", "Test1234!", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "email");
    }

    @Test
    @DisplayName("회원가입 요청 fail - password가 빈칸이면 검증에 실패한다")
    void signupRequest_fail_blankPassword() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password");
    }

    @Test
    @DisplayName("회원가입 요청 fail - password가 8자 미만이면 검증에 실패한다")
    void signupRequest_fail_shortPassword() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "T1!", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password");
    }

    @Test
    @DisplayName("회원가입 요청 fail - password가 20자를 초과하면 검증에 실패한다")
    void signupRequest_fail_longPassword() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "Test1234!Test1234!Test1234!", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password");
    }

    @Test
    @DisplayName("회원가입 요청 fail - password에 영문자가 없으면 검증에 실패한다")
    void signupRequest_fail_passwordWithoutLetter() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "12345678!", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password");
    }

    @Test
    @DisplayName("회원가입 요청 fail - password에 숫자가 없으면 검증에 실패한다")
    void signupRequest_fail_passwordWithoutNumber() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "TestPassword!", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password");
    }

    @Test
    @DisplayName("회원가입 요청 fail - password에 특수문자가 없으면 검증에 실패한다")
    void signupRequest_fail_passwordWithoutSpecialCharacter() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "Test12345", null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password");
    }

    private void assertHasViolation(
            Set<ConstraintViolation<SignupRequest>> violations,
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
