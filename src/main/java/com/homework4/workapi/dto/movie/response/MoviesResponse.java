package com.homework4.workapi.dto.movie.response;

import com.homework4.workapi.entity.Movie;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class MoviesResponse {

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private final Long id;
    private final String title;
    private final String posterUrl;
    private final LocalDate releaseDate;
    private final String certification;
    private final BigDecimal rating;
    private final int runningTime;
    private final String genre;
    private final String overview;
    private final String director;
    private final int ranking;

    public MoviesResponse(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.posterUrl = POSTER_BASE_URL + movie.getPosterPath();
        this.releaseDate = movie.getReleaseDate();
        this.certification = movie.getCertification();
        this.rating = movie.getTmdbRating();
        this.runningTime = movie.getRunningTime();
        this.genre = movie.getGenre();
        this.overview = movie.getOverview();
        this.director = movie.getDirector();
        this.ranking = movie.getRanking();
    }
}