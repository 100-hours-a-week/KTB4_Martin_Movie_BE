package com.homework4.workapi.authTest;

import com.homework4.workapi.auth.JwtProperties;
import com.homework4.workapi.auth.JwtProvider;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-must-be-at-least-32-bytes-long");
        properties.setAccessTokenExpSeconds(1234);
        properties.setRefreshTokenExpSeconds(5678);

        jwtProvider = new JwtProvider(properties);
        jwtProvider.init();
    }

    @Test
    @DisplayName("Access Token은 사용자 식별자와 사용자 정보를 복원할 수 있다")
    void createAccessToken_restoresSubjectAndClaims() {
        String token = jwtProvider.createAccessToken(
                1L,
                "kim@test.com",
                "kim"
        );

        assertTrue(jwtProvider.isAccessToken(token));
        assertEquals(1L, jwtProvider.getUserId(token));
        assertEquals(
                "kim@test.com",
                jwtProvider.parse(token).getPayload().get("email", String.class)
        );
        assertEquals(
                "kim",
                jwtProvider.parse(token).getPayload().get("nickname", String.class)
        );
    }

    @Test
    @DisplayName("Refresh Token은 Access Token으로 판별되지 않으며 사용자를 복원할 수 있다")
    void createRefreshToken_isNotAccessToken() {
        String token = jwtProvider.createRefreshToken(1L);

        assertFalse(jwtProvider.isAccessToken(token));
        assertEquals(1L, jwtProvider.getUserId(token));
        assertNull(jwtProvider.parse(token).getPayload().get("email", String.class));
        assertNull(jwtProvider.parse(token).getPayload().get("nickname", String.class));
    }

    @Test
    @DisplayName("서명이 변조된 토큰은 파싱할 수 없다")
    void parse_rejectsTamperedToken() {
        String token = jwtProvider.createAccessToken(
                1L,
                "kim@test.com",
                "kim"
        );

        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");

        assertThrows(
                JwtException.class,
                () -> jwtProvider.parse(tamperedToken)
        );
    }

    @Test
    @DisplayName("Access Token의 유효 기간을 밀리초 단위로 반환한다")
    void getAccessTokenValidityInMilliseconds_convertsSecondsToMilliseconds() {
        assertEquals(
                1_234_000L,
                jwtProvider.getAccessTokenValidityInMilliseconds()
        );
    }
}