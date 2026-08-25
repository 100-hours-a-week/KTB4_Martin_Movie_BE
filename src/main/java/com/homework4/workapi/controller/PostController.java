package com.homework4.workapi.controller;

import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.common.PageResponse;
import com.homework4.workapi.dto.post.request.PostRequest;
import com.homework4.workapi.dto.post.response.PostLikeResponse;
import com.homework4.workapi.dto.post.response.PostListResponse;
import com.homework4.workapi.dto.post.response.PostResponse;
import com.homework4.workapi.dto.post.request.UpdatePostRequest;
import com.homework4.workapi.dto.post.response.PostsPreviewResponse;
import com.homework4.workapi.service.PostSearchService;
import com.homework4.workapi.service.PostService;
import com.homework4.workapi.validation.ValidationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostSearchService postSearchService;

    @PostMapping
    public CommonResponse<PostResponse> addPost(@AuthenticationPrincipal Long userId, @Valid @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.addPost(userId, postRequest);
        if(postResponse == null) {
            return new CommonResponse<>("존재하지 않는 사용자 입니다.", null);
        }
        return new CommonResponse<>("게시글을 등록하였습니다.", postResponse);
    }

    @GetMapping
    public CommonResponse<PageResponse<PostListResponse>> getPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1")
            @Min( value = ValidationConstants.MIN_PAGE, message = ValidationConstants.PAGE_MIN_MESSAGE ) int page
    ) {
        PageResponse<PostListResponse> posts = PageResponse.from(postService.getPosts(userId, page));

        return new CommonResponse<>(
                "게시글 목록을 조회 하였습니다.",
                posts
        );
    }

    @GetMapping("/preview")
    public CommonResponse<List<PostsPreviewResponse>> getPostsPreview(){
        List<PostsPreviewResponse> postsPreview = postService.getPostsPreview();
        return new CommonResponse<>(
                "홈 게시글 목록을 조회 하였습니다.", postsPreview
        );
    }

    @GetMapping("/{postId}")
    public CommonResponse<PostResponse> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {

        PostResponse post = postService.getPost(postId, userId);
        return new CommonResponse<>("게시글을 조회 하였습니다.", post);
    }
    @PatchMapping("/{postId}")
    public CommonResponse<PostResponse> updatePost(@Valid @PathVariable Long postId, @AuthenticationPrincipal  Long userId, @Valid @RequestBody UpdatePostRequest postRequest) {
        PostResponse postResponse = postService.updatePost(postId, userId, postRequest);
        return new CommonResponse<>("게시글을 수정하였습니다.", postResponse);
    }

    @DeleteMapping("/{postId}")
    public CommonResponse<PostResponse> deletePost(@PathVariable Long postId, @AuthenticationPrincipal Long userId) {
        PostResponse postResponse = postService.deletePost(postId, userId);
        return new CommonResponse<>("삭제 되었습니다.", postResponse);
    }

    @PostMapping("/{postId}/like")
    public CommonResponse<PostLikeResponse> likePost(@PathVariable Long postId, @AuthenticationPrincipal Long userId) {
        PostLikeResponse response = postService.likePost(postId, userId);
        return new CommonResponse<>("좋아요를 눌렀습니다.", response);
    }

    @PostMapping("/{postId}/unlike")
    public CommonResponse<PostLikeResponse> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal Long userId) {
        PostLikeResponse response = postService.unlikePost(postId, userId);
        return new CommonResponse<>("좋아요를 취소하였습니다.", response);
    }

    @PostMapping("/{postId}/views")
    public CommonResponse<Long> updatePostView(@PathVariable Long postId, @AuthenticationPrincipal Long userId) {
        long viewCount = postService.updatePostView(postId, userId);

        return new CommonResponse<>("조회수가 반영되었습니다.", viewCount);
    }

    @GetMapping("/search")
    public CommonResponse<PageResponse<PostListResponse>> searchPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam
            @Size(max = ValidationConstants.SEARCH_KEYWORD_MAX_LENGTH, message = ValidationConstants.KEYWORD_MAX_MESSAGE)
            String keyword,
            @RequestParam(defaultValue = "1")
            @Min(value = ValidationConstants.MIN_PAGE, message = ValidationConstants.PAGE_MIN_MESSAGE)
            @Max(value = ValidationConstants.MAX_SEARCH_PAGE, message = ValidationConstants.PAGE_MAX_MESSAGE)
            int page
    ) {
        PageResponse<PostListResponse> posts =
                PageResponse.from(postSearchService.searchPosts(userId, page, keyword));

        return new CommonResponse<>("게시글 검색 결과를 조회하였습니다.", posts);
    }
}
