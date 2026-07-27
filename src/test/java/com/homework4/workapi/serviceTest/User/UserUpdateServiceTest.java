package com.homework4.workapi.serviceTest.User;

import com.homework4.workapi.dto.user.request.UpdatePasswordRequest;
import com.homework4.workapi.dto.user.request.UpdateUserRequest;
import com.homework4.workapi.dto.user.response.UserResponse;
import com.homework4.workapi.entity.User;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUpdateServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("회원정보 수정 success - 기존 닉네임과 같으면 중복 검사 없이 성공한다")
    void updateUser_success_sameUsername() {
        Long userId = 1L;

        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", userId);

        UpdateUserRequest request = new UpdateUserRequest("kim");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("kim")).thenReturn(false);

        UserResponse response = userService.updateUser(userId, request);

        assertEquals("kim", response.getUsername());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByUsername("kim");
    }

    @Test
    @DisplayName("회원정보 수정 success - 중복되지 않은 새 닉네임이면 수정에 성공한다")
    void updateUser_success_newUsername() {
        Long userId = 1L;

        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", userId);

        UpdateUserRequest request = new UpdateUserRequest("lee");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("lee")).thenReturn(false);

        UserResponse response = userService.updateUser(userId, request);

        assertEquals("lee", response.getUsername());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByUsername("lee");
    }


    @Test
    @DisplayName("회원정보 수정 fail - 새 닉네임이 이미 사용 중이면 실패한다")
    void updateUser_fail_duplicateUsername() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest("lee");

        User user = new User("kim", "kim@test.com", "encodedPassword", null);
        ReflectionTestUtils.setField(user, "id", userId);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("lee")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateUser(1L, request)
        );

        assertEquals(409, exception.getStatusCode().value());

        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("lee");
    }

    @Test
    @DisplayName("회원정보 수정 fail - 사용자가 존재하지 않으면 실패한다")
    void updateUser_fail_userNotFound() {
        Long userId = 999L;

        UpdateUserRequest request = new UpdateUserRequest("lee");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateUser(userId, request)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    @DisplayName("비밀번호 수정 success - 새 비밀번호를 암호화해서 저장한다.")
    void updatePassword_success() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("New1234!");

        User user = new User("kim", "kim@test.com", "oldEncodedPassword", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("New1234!")).thenReturn("newEncodedPassword");

        // when
        UserResponse response = userService.updatePassword(1L, request);

        // then
        assertEquals("kim", response.getUsername());
        assertEquals("kim@test.com", response.getEmail());
        assertEquals("newEncodedPassword", user.getPassword());

        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("New1234!");
    }

    @Test
    @DisplayName("비밀번호 수정 fail - 사용자가 없으면 실패한다.")
    void updatePassword_fail_userNotFound() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("New1234!");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.updatePassword(1L, request)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, never()).encode(anyString());
    }
}
