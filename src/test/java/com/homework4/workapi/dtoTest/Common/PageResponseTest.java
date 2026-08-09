package com.homework4.workapi.dtoTest.Common;

import com.homework4.workapi.dto.common.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageResponseTest {

    @Test
    void from_mapsPageMetadata() {
        Page<String> pageData = new PageImpl<>(
                List.of("first", "second"),
                PageRequest.of(0, 2),
                5
        );

        PageResponse<String> response = PageResponse.from(pageData);

        assertEquals(List.of("first", "second"), response.content());
        assertEquals(1, response.page());
        assertEquals(2, response.size());
        assertEquals(3, response.totalPages());
        assertEquals(5L, response.totalElements());
        assertTrue(response.first());
        assertFalse(response.last());
    }

    @Test
    void content_isImmutable() {
        PageResponse<String> response = new PageResponse<>(
                new ArrayList<>(List.of("item")),
                1,
                10,
                1,
                1L,
                true,
                true
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> response.content().add("new item")
        );
    }
}