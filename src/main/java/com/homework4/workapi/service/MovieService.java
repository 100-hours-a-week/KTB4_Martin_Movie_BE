package com.homework4.workapi.service;

import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.movie.response.MovieResponse;
import com.homework4.workapi.dto.movie.response.MoviesResponse;
import com.homework4.workapi.entity.Movie;
import com.homework4.workapi.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private static final int PAGE_SIZE = 20;

    private final MovieRepository movieRepository;

    public Page<MoviesResponse> getMovies(int page) {
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지는 1 이상이어야 합니다.");
        }

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE,
                Sort.by(Sort.Order.desc("releaseDate"), Sort.Order.desc("id"))
        );

        return movieRepository.findAll(pageable).map(MoviesResponse::new);
    }
}