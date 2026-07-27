package com.homework4.workapi.serviceTest.User;

import com.homework4.workapi.dto.user.request.SignupRequest;
import com.homework4.workapi.dto.user.response.UserResponse;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.repository.UserRepository;
import com.homework4.workapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSignupServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("회원가입 success - 회원가입 성공시 response를 반환 한다.")
    void signup_success() {
        SignupRequest request = new SignupRequest("kim", "kim@test.com", "Test1234!", null);

        when(userRepository.existsByEmail("kim@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("kim")).thenReturn(false);
        when(passwordEncoder.encode("Test1234!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.signup(request);

        assertEquals("kim", response.getUsername());
        assertEquals("kim@test.com", response.getEmail());

        verify(passwordEncoder).encode("Test1234!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 fail - 이메일 중복시 회원가입에 실패 한다.")
    void signup_fail_duplication_email() {
        SignupRequest request = new SignupRequest("anotherKim", "kim@test.com", "Test1234!", null);

        when(userRepository.existsByEmail("kim@test.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.signup(request)
        );

        assertEquals(409, exception.getStatusCode().value());

        verify(userRepository, times(1)).existsByEmail("kim@test.com");
        verify(userRepository, never()).existsByUsername(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 fail - 닉네임 중복시 회원가입에 실패 한다.")
    void signup_fail_duplication_userName() {
        SignupRequest request = new SignupRequest("kim", "anotherKim@test.com", "Test1234!", null);

        when(userRepository.existsByEmail("anotherKim@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("kim")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.signup(request)
        );

        assertEquals(409, exception.getStatusCode().value());

        verify(userRepository, times(1)).existsByEmail("anotherKim@test.com");
        verify(userRepository, times(1)).existsByUsername("kim");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
