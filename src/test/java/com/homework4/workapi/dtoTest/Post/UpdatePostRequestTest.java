package com.homework4.workapi.dtoTest.Post;

import com.homework4.workapi.dto.post.request.UpdatePostRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdatePostRequestTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("게시글 수정 요청 success - 입력값이 모두 유효하다")
    void updatePostRequest_success() {
        UpdatePostRequest request =
                createRequest("수정 제목", "수정 내용", 7);

        Set<ConstraintViolation<UpdatePostRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("게시글 수정 요청 fail - 제목이 비어 있으면 실패한다")
    void updatePostRequest_fail_blankTitle() {
        UpdatePostRequest request =
                createRequest("", "수정 내용", 7);

        Set<ConstraintViolation<UpdatePostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "title");
    }

    @Test
    @DisplayName("게시글 수정 요청 fail - 내용이 비어 있으면 실패한다")
    void updatePostRequest_fail_blankContent() {
        UpdatePostRequest request =
                createRequest("수정 제목", "", 7);

        Set<ConstraintViolation<UpdatePostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "content");
    }

    @Test
    @DisplayName("게시글 수정 요청 fail - 별점이 null이면 실패한다")
    void updatePostRequest_fail_nullRating() {
        UpdatePostRequest request =
                createRequest("수정 제목", "수정 내용", null);

        Set<ConstraintViolation<UpdatePostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "rating");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    @DisplayName("게시글 수정 요청 fail - 별점이 범위를 벗어나면 실패한다")
    void updatePostRequest_fail_ratingOutOfRange(int rating) {
        UpdatePostRequest request =
                createRequest("수정 제목", "수정 내용", rating);

        Set<ConstraintViolation<UpdatePostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "rating");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    @DisplayName("게시글 수정 요청 success - 별점 경계값이 유효하다")
    void updatePostRequest_success_ratingBoundary(int rating) {
        UpdatePostRequest request =
                createRequest("수정 제목", "수정 내용", rating);

        Set<ConstraintViolation<UpdatePostRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    private UpdatePostRequest createRequest(
            String title,
            String content,
            Integer rating
    ) {
        return new UpdatePostRequest(title, content, rating);
    }

    private void assertHasViolation(
            Set<ConstraintViolation<UpdatePostRequest>> violations,
            String fieldName
    ) {
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath()
                        .toString()
                        .equals(fieldName)
        ));
    }
}
