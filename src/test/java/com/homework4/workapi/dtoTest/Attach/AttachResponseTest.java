package com.homework4.workapi.dtoTest.Attach;

import com.homework4.workapi.dto.attach.response.AttachResponse;
import com.homework4.workapi.entity.Attach;
import com.homework4.workapi.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AttachResponseTest {

    @Test
    @DisplayName("첨부파일 엔티티의 필드를 응답 DTO로 변환한다")
    void from_mapsAttachFieldsToResponse() {
        Post post = mock(Post.class);
        when(post.getId()).thenReturn(10L);

        Attach attach = new Attach(
                post,
                "https://example.com/images/poster.png",
                "upload-key"
        );
        LocalDateTime attachTime = LocalDateTime.of(2026, 8, 7, 12, 0);

        ReflectionTestUtils.setField(attach, "id", 1L);
        ReflectionTestUtils.setField(attach, "attachTime", attachTime);

        AttachResponse response = AttachResponse.from(attach);

        assertEquals(1L, response.id());
        assertEquals(10L, response.postId());
        assertEquals("https://example.com/images/poster.png", response.attachUrl());
        assertEquals(attachTime, response.attachTime());
    }
}
