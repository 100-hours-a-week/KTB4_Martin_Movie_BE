package com.homework4.workapi.dtoTest.User;

import com.homework4.workapi.dto.user.response.UserResponse;
import com.homework4.workapi.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserResponseTest {

    @Test
    @DisplayName("일반 사용자 엔티티의 필드를 응답 DTO로 변환한다")
    void from_mapsActiveUserFields() {
        User user = new User(
                "kim",
                "kim@test.com",
                "encoded-password",
                "https://example.com/profile.png"
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        UserResponse response = UserResponse.from(user);

        assertEquals(1L, response.id());
        assertEquals("kim", response.username());
        assertEquals("kim@test.com", response.email());
        assertEquals("https://example.com/profile.png", response.profileImageUrl());
    }

    @Test
    @DisplayName("탈퇴 사용자의 이메일과 프로필 이미지는 응답에 노출하지 않는다")
    void from_hidesPersonalInformationForDeletedUser() {
        User user = new User(
                "kim",
                "kim@test.com",
                "encoded-password",
                "https://example.com/profile.png"
        );
        ReflectionTestUtils.setField(user, "id", 1L);
        user.softDelete();

        UserResponse response = UserResponse.from(user);

        assertEquals(1L, response.id());
        assertEquals("(알 수 없음)", response.username());
        assertNull(response.email());
        assertNull(response.profileImageUrl());
    }
}
