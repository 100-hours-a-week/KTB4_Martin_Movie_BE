package com.homework4.workapi.dto.movie.response;

import com.homework4.workapi.entity.Movie;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
public class MovieResponse {

    private static final String POSTER_BASE_URL =
            "https://image.tmdb.org/t/p/w500";

    private final Long id;
    private final Long tmdbId;
    private final String title;
    private final String posterUrl;
    private final int runningTime;
    private final LocalDate releaseDate;
    private final String certification;
    private final String genre;
    private final String overview;
    private final String director;
    private final BigDecimal rating;
    private final List<String> actors;
    private final int ranking;

    public MovieResponse(Movie movie) {
        this.id = movie.getId();
        this.tmdbId = movie.getTmdbId();
        this.title = movie.getTitle();
        this.posterUrl = POSTER_BASE_URL + movie.getPosterPath();
        this.runningTime = movie.getRunningTime();
        this.releaseDate = movie.getReleaseDate();
        this.certification = movie.getCertification();
        this.genre = movie.getGenre();
        this.overview = movie.getOverview();
        this.director = movie.getDirector();
        this.rating = movie.getTmdbRating();
        this.actors = List.copyOf(movie.getActors());
        this.ranking = movie.getRanking();
    }
}