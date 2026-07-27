package com.homework4.workapi.dto.comment.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
    @NotBlank(message = "내용은 필수 입니다.")
    String content
) {
}
