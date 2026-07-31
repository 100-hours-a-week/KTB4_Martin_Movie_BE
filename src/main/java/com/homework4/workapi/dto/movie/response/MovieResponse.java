package com.homework4.workapi.dto.movie.response;

import com.homework4.workapi.entity.Movie;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MovieResponse(
        Long id,
        Long tmdbId,
        String title,
        String posterUrl,
        int runningTime,
        LocalDate releaseDate,
        String certification,
        String genre,
        String overview,
        String director,
        BigDecimal rating,
        List<String> actors,
        int ranking
) {

    private static final String POSTER_BASE_URL =
            "https://image.tmdb.org/t/p/w500";

    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTmdbId(),
                movie.getTitle(),
                POSTER_BASE_URL + movie.getPosterPath(),
                movie.getRunningTime(),
                movie.getReleaseDate(),
                movie.getCertification(),
                movie.getGenre(),
                movie.getOverview(),
                movie.getDirector(),
                movie.getTmdbRating(),
                List.copyOf(movie.getActors()),
                movie.getRanking()
        );
    }
}
