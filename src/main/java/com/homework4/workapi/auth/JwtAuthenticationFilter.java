package com.homework4.workapi.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 토큰 검증 및 인증 설정
            try {
                // 토큰이 유효한 액세스 토큰인지 확인
                if (jwtProvider.isAccessToken(token)) {
                    // 토큰에서 사용자 ID 추출
                    Long userId = jwtProvider.getUserId(token);

                    // 인증 객체 생성 및 SecurityContext에 설정
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,     // principal
                                    null,
                                    List.of()   // 권한 생성시 사용, 현재는 빈 리스트
                            );

                    // SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            } catch (Exception exception) {
                SecurityContextHolder.clearContext();
            }
        }

        // 토큰이 없거나, 유효한 경우 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}