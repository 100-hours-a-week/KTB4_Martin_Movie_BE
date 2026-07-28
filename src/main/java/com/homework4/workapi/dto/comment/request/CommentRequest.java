package com.homework4.workapi.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
    @NotBlank(message = "내용은 필수 입니다.")
    @Size(max = 1000, message = "1000자 이상 작성 하실 수 없습니다.")
    String content
) {
}
