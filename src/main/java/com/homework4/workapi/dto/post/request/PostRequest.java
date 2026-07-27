package com.homework4.workapi.dto.post.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostRequest(
    @NotBlank(message = "제목은 필수 입니다.")
    String title,

    @NotBlank(message = "내용은 필수 입니다.")
    String content,

    @NotNull(message = "별점 입력은 필수 입니다.")
    @Min(value = 1, message = "최소 별점은 1점 입니다.")
    @Max(value = 10, message = "최대 별점은 10점 입니다.")
    Integer rating
) {
}
