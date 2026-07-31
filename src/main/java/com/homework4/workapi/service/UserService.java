package com.homework4.workapi.service;

import com.homework4.workapi.auth.JwtProvider;
import com.homework4.workapi.dto.auth.request.LoginRequest;
import com.homework4.workapi.dto.auth.response.LoginResponse;
import com.homework4.workapi.dto.auth.response.TokenInfo;
import com.homework4.workapi.dto.auth.result.LoginResult;
import com.homework4.workapi.dto.auth.result.TokenResult;
import com.homework4.workapi.dto.user.request.SignupRequest;
import com.homework4.workapi.dto.user.request.UpdatePasswordRequest;
import com.homework4.workapi.dto.user.request.UpdateUserRequest;
import com.homework4.workapi.dto.user.response.UserResponse;
import com.homework4.workapi.entity.RefreshToken;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.exception.AuthorizedException;
import com.homework4.workapi.repository.RefreshTokenRepository;
import com.homework4.workapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    @Transactional
    public UserResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByUsername(signupRequest.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        MultipartFile profileImage = signupRequest.profileImage();
        String profileImageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = fileService.saveImage(profileImage);
        }

        User user = new User(signupRequest.username(), signupRequest.email(), passwordEncoder.encode(signupRequest.password()), profileImageUrl);

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    @Transactional
    public LoginResult login(LoginRequest loginRequest) {
        User user = userRepository
                .findByEmailAndDeletedFalse(loginRequest.email())
                .orElseThrow(() ->
                        new AuthorizedException("INVALID_CREDENTIALS")
                );
        if (!passwordEncoder.matches(
                loginRequest.password(),
                user.getPassword()
        )) {
            throw new AuthorizedException("INVALID_CREDENTIALS");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );

        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(
                new RefreshToken(
                        refreshToken,
                        user.getId(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new LoginResult(
                LoginResponse.of(UserResponse.from(user), accessToken, jwtProvider.getAccessTokenValidityInMilliseconds()),
                refreshToken
        );
    }

    // 액세스 토큰 재발급
    @Transactional
    public TokenResult refreshAccessToken(String refreshToken) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthorizedException("UNAUTHORIZED"));

        if (saved.isExpired()) {
            refreshTokenRepository.delete(saved);
            throw new AuthorizedException("UNAUTHORIZED");
        }

        User user = userRepository.findById(saved.getUserId())
                .filter(foundUser -> !foundUser.isDeleted())
                .orElseThrow(() ->
                        new AuthorizedException("UNAUTHORIZED")
                );
        String newAccessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );

        // Refresh Token 회전 (Rotation)
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.delete(saved);
        refreshTokenRepository.save(
                new RefreshToken(
                        newRefreshToken,
                        user.getId(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new TokenResult(
                new TokenInfo(newAccessToken, 3600),
                newRefreshToken
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }


    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {

        User user = findUserById(userId);
        String newUsername = updateUserRequest.username();

        if(userRepository.existsByUsername(newUsername)&&!newUsername.equals(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "사용 중인 닉네임 입니다.");
        }

        user.updateUsername(newUsername);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        if (user.isDeleted()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 탈퇴한 사용자입니다."
            );
        }

        user.softDelete();
        refreshTokenRepository.deleteByUserId(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updatePassword(Long userId, UpdatePasswordRequest updatePasswordRequest) {
        User user = findUserById(userId);
        user.updatePassword(passwordEncoder.encode(updatePasswordRequest.newPassword()));
        refreshTokenRepository.deleteByUserId(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfileImage(Long userId, MultipartFile profileImageUrl) {
        User user = findUserById(userId);
        String previousProfileImageUrl = user.getProfileImageUrl();

        String newProfileImageUrl = fileService.saveImage(profileImageUrl);
        user.updateProfileImage(newProfileImageUrl);

        if(previousProfileImageUrl != null){
            fileService.deleteImage(previousProfileImageUrl);
        }

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse deleteProfileImage(Long userId) {
        User user = findUserById(userId);
        String PreviousProfileImageUrl = user.getProfileImageUrl();

        fileService.deleteImage(PreviousProfileImageUrl);
        user.removeProfileImage();

        return UserResponse.from(user);
    }

    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다.");
        }
        return user.get();
    }
}
