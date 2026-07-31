package com.homework4.workapi.dto.movie.response;

import com.homework4.workapi.entity.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MoviesResponse(
        Long id,
        String title,
        String posterUrl,
        LocalDate releaseDate,
        String certification,
        BigDecimal rating,
        int runningTime,
        String genre,
        String overview,
        String director,
        int ranking
) {
    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public static MoviesResponse from(Movie movie) {
        return new MoviesResponse(
                movie.getId(),
                movie.getTitle(),
                POSTER_BASE_URL + movie.getPosterPath(),
                movie.getReleaseDate(),
                movie.getCertification(),
                movie.getTmdbRating(),
                movie.getRunningTime(),
                movie.getGenre(),
                movie.getOverview(),
                movie.getDirector(),
                movie.getRanking()
        );
    }
}
