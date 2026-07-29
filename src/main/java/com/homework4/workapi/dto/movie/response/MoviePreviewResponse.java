package com.homework4.workapi.dto.movie.response;

import com.homework4.workapi.entity.Movie;
import lombok.Getter;

@Getter
public class MoviePreviewResponse {
    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private Long id;
    private String title;
    private String posterUrl;

    public MoviePreviewResponse(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.posterUrl = POSTER_BASE_URL + movie.getPosterPath();

    }
}
