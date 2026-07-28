package com.homework4.workapi.dto.movie.response;

import com.homework4.workapi.entity.Movie;
import com.homework4.workapi.entity.Post;
import jakarta.persistence.Column;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MovieResponse {


    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private final Long id;
    private final String title;
    private final String posterPath;
    private final LocalDate releaseDate;
    private final String certification;
    private final String genre;
    private final String overview;
    private final String director;
    private final BigDecimal tmdbRating;
    private final List<String> actors;
    private final int runningTime;

    public MovieResponse(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.posterPath = POSTER_BASE_URL + movie.getPosterPath();
        this.releaseDate = movie.getReleaseDate();
        this.certification = movie.getCertification();
        this.genre = movie.getGenre();
        this.overview = movie.getOverview();
        this.director = movie.getDirector();
        this.tmdbRating = movie.getTmdbRating();
        this.actors = movie.getActors();
        this.runningTime = movie.getRunningTime();
    }

}
