package com.homework4.workapi.controller;

import com.homework4.workapi.dto.comment.request.CommentRequest;
import com.homework4.workapi.dto.comment.response.CommentResponse;
import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.common.PageResponse;
import com.homework4.workapi.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public CommonResponse<CommentResponse> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CommentRequest commentRequest
    ) {
        CommentResponse commentResponse = commentService.addComment(commentRequest, postId, userId);

        if(commentResponse == null) {
            return new CommonResponse<>("존재하지 않은 사용자 또는 게시글 입니다.", null);
        }
        return new CommonResponse<>("댓글이 작성되었습니다.", commentResponse);
    }

    @GetMapping
    public CommonResponse<PageResponse<CommentResponse>> getComments(@PathVariable Long postId, @RequestParam(defaultValue = "1") int page) {
        PageResponse<CommentResponse> comments = PageResponse.from(commentService.getComments(postId, page));

        return new CommonResponse<>("댓글 목록을 조회하였습니다.", comments);
    }

    @PutMapping("/{commentId}")
    public CommonResponse<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CommentRequest commentRequest
    ) {
        CommentResponse commentResponse = commentService.updateComment(commentId, commentRequest, postId, userId);
        return new CommonResponse<>("댓글을 수정 했습니다.",commentResponse);
    }

    @DeleteMapping("/{commentId}")
    public CommonResponse<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId
    ) {
        commentService.deleteComment(postId, commentId, userId);
        return new CommonResponse<>("댓글을 삭제 했습니다.", null);
    }
}