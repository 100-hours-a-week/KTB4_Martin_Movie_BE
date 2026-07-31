package com.homework4.workapi.dto.movie.response;

import com.homework4.workapi.entity.Movie;

public record MoviePreviewResponse(
        Long id,
        String title,
        String posterUrl
) {
    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public static MoviePreviewResponse from(Movie movie) {
        return new MoviePreviewResponse(
                movie.getId(),
                movie.getTitle(),
                POSTER_BASE_URL + movie.getPosterPath()
        );
    }
}
