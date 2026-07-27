package com.homework4.workapi.dtoTest.Post;

import com.homework4.workapi.dto.post.request.PostRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PostRequestTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("게시글 생성 요청 success - 모든 입력값이 유효하다")
    void postRequest_success() {
        PostRequest request =
                createRequest("게시글 제목", "게시글 내용", 7);

        Set<ConstraintViolation<PostRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("게시글 생성 요청 fail - 제목이 비어 있으면 실패한다")
    void postRequest_fail_blankTitle() {
        PostRequest request =
                createRequest("", "게시글 내용", 7);

        Set<ConstraintViolation<PostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "title");
    }

    @Test
    @DisplayName("게시글 생성 요청 fail - 내용이 비어 있으면 실패한다")
    void postRequest_fail_blankContent() {
        PostRequest request =
                createRequest("게시글 제목", "", 7);

        Set<ConstraintViolation<PostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "content");
    }

    @Test
    @DisplayName("게시글 생성 요청 fail - 별점이 null이면 실패한다")
    void postRequest_fail_nullRating() {
        PostRequest request =
                createRequest("게시글 제목", "게시글 내용", null);

        Set<ConstraintViolation<PostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "rating");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    @DisplayName("게시글 생성 요청 fail - 별점이 범위를 벗어나면 실패한다")
    void postRequest_fail_ratingOutOfRange(int rating) {
        PostRequest request =
                createRequest("게시글 제목", "게시글 내용", rating);

        Set<ConstraintViolation<PostRequest>> violations =
                validator.validate(request);

        assertHasViolation(violations, "rating");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    @DisplayName("게시글 생성 요청 success - 별점 경계값이 유효하다")
    void postRequest_success_ratingBoundary(int rating) {
        PostRequest request =
                createRequest("게시글 제목", "게시글 내용", rating);

        Set<ConstraintViolation<PostRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    private PostRequest createRequest(
            String title,
            String content,
            Integer rating
    ) {
        return new PostRequest(title, content, rating);
    }

    private void assertHasViolation(
            Set<ConstraintViolation<PostRequest>> violations,
            String fieldName
    ) {
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath()
                        .toString()
                        .equals(fieldName)
        ));
    }
}
