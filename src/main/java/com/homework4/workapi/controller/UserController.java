package com.homework4.workapi.controller;

import com.homework4.workapi.dto.auth.request.LoginRequest;
import com.homework4.workapi.dto.auth.response.LoginResponse;
import com.homework4.workapi.dto.auth.response.TokenInfo;
import com.homework4.workapi.dto.auth.result.LoginResult;
import com.homework4.workapi.dto.auth.result.TokenResult;
import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.user.request.SignupRequest;
import com.homework4.workapi.dto.user.request.UpdatePasswordRequest;
import com.homework4.workapi.dto.user.request.UpdateUserRequest;
import com.homework4.workapi.dto.user.response.UserResponse;
import com.homework4.workapi.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<UserResponse> signup(@Valid @ModelAttribute SignupRequest signupRequest) {
        UserResponse response = userService.signup(signupRequest);
        return new CommonResponse<>("회원 가입에 성공 하였습니다.", response);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse
    ) {
        LoginResult result = userService.login(loginRequest);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>("로그인에 성공하였습니다.", result.getResponse()));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<CommonResponse<TokenInfo>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        TokenResult result = userService.refreshAccessToken(refreshToken);

        // Refresh Token 회전 시 새 쿠키 세팅
        if (result.getNewRefreshToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getNewRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(14 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>("로그인 성공", result.getToken()));
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(
            @AuthenticationPrincipal
            Long userId,
            HttpServletResponse httpResponse
    ){
        userService.logout(userId);
        ResponseCookie expiredCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
        return new CommonResponse<>("로그아웃 되었습니다.", null);
    }

    @PatchMapping("/me")
    public CommonResponse<UserResponse> updateUser(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserResponse userResponse = userService.updateUser(userId, updateUserRequest);
        return new CommonResponse<>("회원정보가 수정되었습니다.", userResponse);
    }

    @PatchMapping("/me/password")
    public CommonResponse<UserResponse> updatePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        UserResponse userResponse = userService.updatePassword(userId, updatePasswordRequest);
        return new CommonResponse<>("비밀번호가 수정되었습니다.", userResponse);
    }

    @DeleteMapping("/me")
    public CommonResponse<UserResponse> deleteUser(@AuthenticationPrincipal Long userId) {
        UserResponse userResponse = userService.deleteUser(userId);
        return new CommonResponse<>("회원 탈퇴에 성공하였습니다.", userResponse);
    }

    @PutMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<UserResponse> updateProfileImage(
            @AuthenticationPrincipal Long userId,
            @RequestParam("file") MultipartFile profileImage
    ) {
        return new CommonResponse<>("프로필 이미지가 수정 되었습니다.", userService.updateProfileImage(userId, profileImage));
    }

    @DeleteMapping("/me/profile-image")
    public CommonResponse<UserResponse> deleteProfileImage(
            @AuthenticationPrincipal Long userId
    ){
        return new CommonResponse<>("프로필 이미지가 삭제 되었습니다.", userService.deleteProfileImage(userId));
    }
}