package com.homework4.workapi.controller;

import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.movie.response.MoviesResponse;
import com.homework4.workapi.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public CommonResponse<Page<MoviesResponse>> getMovies(@RequestParam(defaultValue = "1") int page
    ) {
        Page<MoviesResponse> movies = movieService.getMovies(page);
        return new CommonResponse<>("영화 목록을 조회하였습니다.", movies);
    }
}