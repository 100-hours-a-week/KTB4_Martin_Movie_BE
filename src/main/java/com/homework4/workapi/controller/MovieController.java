package com.homework4.workapi.controller;

import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.common.PageResponse;
import com.homework4.workapi.dto.movie.response.MoviePreviewResponse;
import com.homework4.workapi.dto.movie.response.MovieResponse;
import com.homework4.workapi.dto.movie.response.MoviesResponse;
import com.homework4.workapi.service.MovieService;
import com.homework4.workapi.validation.ValidationConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public CommonResponse<PageResponse<MoviesResponse>> getMovies(
            @RequestParam(defaultValue = "1")
            @Min(value = ValidationConstants.MIN_PAGE, message = ValidationConstants.PAGE_MIN_MESSAGE)
            int page
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

    @GetMapping("/search")
    public CommonResponse<PageResponse<MoviesResponse>> searchMovies(
            @RequestParam
            @Size(max = ValidationConstants.SEARCH_KEYWORD_MAX_LENGTH, message = ValidationConstants.KEYWORD_MAX_MESSAGE)
            String keyword,
            @RequestParam(defaultValue = "1")
            @Min(value = ValidationConstants.MIN_PAGE, message = ValidationConstants.PAGE_MIN_MESSAGE)
            int page
    ) {
        PageResponse<MoviesResponse> movies = PageResponse.from(movieService.searchMovies(page, keyword));

        return CommonResponse.of("영화 검색 결과를 조회하였습니다.", movies);
    }

    @GetMapping("/{movieId}")
    public CommonResponse<MovieResponse> getMovie(@PathVariable Long movieId) {
        MovieResponse movie = movieService.getMovie(movieId);
        return new CommonResponse<>("영화 상세 정보를 조회하였습니다.", movie);
    }


}
