package com.homework4.workapi.dtoTest.Comment;

import com.homework4.workapi.dto.comment.response.CommentResponse;
import com.homework4.workapi.entity.Comment;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentResponseTest {

    @Test
    @DisplayName("댓글 엔티티의 필드를 응답 DTO로 변환한다")
    void from_mapsCommentFieldsToResponse() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(20L);
        when(user.getDisplayUsername()).thenReturn("kim");
        when(user.getDisplayProfileImageUrl())
                .thenReturn("https://example.com/profile.png");

        Post post = mock(Post.class);
        when(post.getId()).thenReturn(10L);

        Comment comment = new Comment(
                user,
                post,
                "댓글 내용"
        );

        LocalDateTime createTime = LocalDateTime.of(
                2026, 8, 7, 12, 0
        );
        LocalDateTime updateTime = LocalDateTime.of(
                2026, 8, 7, 12, 30
        );

        ReflectionTestUtils.setField(comment, "id", 1L);
        ReflectionTestUtils.setField(comment, "createTime", createTime);
        ReflectionTestUtils.setField(comment, "updateTime", updateTime);

        CommentResponse response = CommentResponse.from(comment);

        assertEquals(1L, response.id());
        assertEquals(10L, response.postId());
        assertEquals(20L, response.userId());
        assertEquals("kim", response.username());
        assertEquals("댓글 내용", response.content());
        assertEquals(createTime, response.createTime());
        assertEquals(updateTime, response.updateTime());
        assertEquals(
                "https://example.com/profile.png",
                response.profileImageUrl()
        );
    }
}
