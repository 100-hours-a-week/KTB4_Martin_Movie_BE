package com.homework4.workapi.dtoTest.Comment;

import com.homework4.workapi.dto.comment.request.CommentRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentRequestTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("댓글 요청 success - 올바른 값이면 검증에 성공한다")
    void commentRequest_success() {
        CommentRequest request = createRequest("댓글 내용");

        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("댓글 요청 fail - content가 빈칸이면 검증에 실패한다")
    void commentRequest_fail_blankContent() {
        CommentRequest request = createRequest("");

        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "content");
    }

    @Test
    @DisplayName("댓글 요청 fail - content가 공백이면 검증에 실패한다")
    void commentRequest_fail_spaceContent() {
        CommentRequest request = createRequest("   ");

        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "content");
    }

    private CommentRequest createRequest(String content) {
        return new CommentRequest(content);
    }

    private void assertHasViolation(
            Set<ConstraintViolation<CommentRequest>> violations,
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
