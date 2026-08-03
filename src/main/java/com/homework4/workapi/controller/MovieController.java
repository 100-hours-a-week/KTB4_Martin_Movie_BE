package com.homework4.workapi.controller;

import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.common.PageResponse;
import com.homework4.workapi.dto.movie.response.MoviePreviewResponse;
import com.homework4.workapi.dto.movie.response.MovieResponse;
import com.homework4.workapi.dto.movie.response.MoviesResponse;
import com.homework4.workapi.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public CommonResponse<PageResponse<MoviesResponse>> getMovies(
            @RequestParam(defaultValue = "1") int page
    ) {
        PageResponse<MoviesResponse> movies = PageResponse.from(
                movieService.getMovies(page)
        );

        return CommonResponse.of("영화 목록을 조회하였습니다.", movies);
    }

    @GetMapping("/preview")
    public CommonResponse<List<MoviePreviewResponse>> getMoviePreviews() {
        List<MoviePreviewResponse> movies = movieService.getMoviePreviews();
        return new CommonResponse<>("홈 영화 정보를 조회하였습니다.", movies);
    }

    @GetMapping("/{movieId}")
    public CommonResponse<MovieResponse> getMovie(@PathVariable Long movieId) {
        MovieResponse movie = movieService.getMovie(movieId);
        return new CommonResponse<>("영화 상세 정보를 조회하였습니다.", movie);
    }
}
