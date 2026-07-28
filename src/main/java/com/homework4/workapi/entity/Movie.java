package com.homework4.workapi.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long tmdbId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String posterPath;

    @Column(nullable = false)
    private int runningTime;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(length = 5000)
    private String overview;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "movie_id"))
    @OrderColumn
    private List<String> actors = new ArrayList<>();

    private String certification;
    private String genre;
    private String director;
    private BigDecimal tmdbRating;
    private int ranking;

    protected Movie(){}

    public Movie(Long tmdbId, String title, String posterPath, int runningTime, LocalDate releaseDate, String certification, String genre, String overview, String director, BigDecimal tmdbRating, List<String> actors, int ranking) {
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterPath = posterPath;
        this.runningTime = runningTime;
        this.releaseDate = releaseDate;
        this.certification = certification;
        this.genre = genre;
        this.overview = overview;
        this.director = director;
        this.actors = new ArrayList<>(actors);
        this.tmdbRating = tmdbRating;
        this.ranking = ranking;
    }

}
