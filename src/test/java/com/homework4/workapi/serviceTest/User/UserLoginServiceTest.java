package com.homework4.workapi.serviceTest.User;

import com.homework4.workapi.auth.JwtProvider;
import com.homework4.workapi.dto.auth.request.LoginRequest;
import com.homework4.workapi.dto.auth.result.LoginResult;
import com.homework4.workapi.entity.RefreshToken;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.exception.AuthorizedException;
import com.homework4.workapi.repository.RefreshTokenRepository;
import com.homework4.workapi.repository.UserRepository;
import com.homework4.workapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("로그인 success - 이메일과 비밀번호가 일치하면 토큰을 반환한다.")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("kim@test.com", "Test1234!");

        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findByEmailAndDeletedFalse("kim@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Test1234!", "encodedPassword")).thenReturn(true);
        when(jwtProvider.createAccessToken(1L, "kim@test.com", "kim")).thenReturn("access-token");
        when(jwtProvider.createRefreshToken(1L)).thenReturn("refresh-token");
        when(jwtProvider.getAccessTokenValidityInMilliseconds()).thenReturn(300000L);

        // when
        LoginResult result = userService.login(request);

        // then
        assertNotNull(result);
        assertEquals("access-token", result.response().token().accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals("kim", result.response().user().username());
        assertEquals("kim@test.com", result.response().user().email());

        verify(userRepository, times(1)).findByEmailAndDeletedFalse("kim@test.com");
        verify(passwordEncoder, times(1)).matches("Test1234!", "encodedPassword");
        verify(jwtProvider, times(1)).createAccessToken(1L, "kim@test.com", "kim");
        verify(jwtProvider, times(1)).createRefreshToken(1L);
        verify(refreshTokenRepository, times(1)).deleteByUserId(1L);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 fail - 이메일에 해당하는 사용자가 없으면 예외가 발생한다.")
    void login_fail_email_not_found() {
        // given
        LoginRequest request = new LoginRequest("unknown@test.com", "Test1234!");

        when(userRepository.findByEmailAndDeletedFalse("unknown@test.com")).thenReturn(Optional.empty());

        // when & then
        assertThrows(AuthorizedException.class, () -> userService.login(request));

        verify(userRepository, times(1)).findByEmailAndDeletedFalse("unknown@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtProvider, never()).createAccessToken(anyLong(), anyString(), anyString());
        verify(jwtProvider, never()).createRefreshToken(anyLong());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 fail - 비밀번호가 일치하지 않으면 예외가 발생한다.")
    void login_fail_wrong_password() {
        // given
        LoginRequest request = new LoginRequest("kim@test.com", "Wrong1234!");

        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findByEmailAndDeletedFalse("kim@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Wrong1234!", "encodedPassword")).thenReturn(false);

        // when & then
        assertThrows(AuthorizedException.class, () -> userService.login(request));

        verify(userRepository, times(1)).findByEmailAndDeletedFalse("kim@test.com");
        verify(passwordEncoder, times(1)).matches("Wrong1234!", "encodedPassword");
        verify(jwtProvider, never()).createAccessToken(anyLong(), anyString(), anyString());
        verify(jwtProvider, never()).createRefreshToken(anyLong());
        verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}
