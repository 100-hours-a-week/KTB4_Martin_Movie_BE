package com.homework4.workapi.service;

import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.dto.movie.response.MoviePreviewResponse;
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
import static com.homework4.workapi.common.PaginationConstants.MOVIE_PAGE_SIZE;
import static com.homework4.workapi.validation.ValidationConstants.MIN_PAGE;
import static com.homework4.workapi.validation.ValidationConstants.PAGE_MIN_MESSAGE;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {


    private final MovieRepository movieRepository;

    public Page<MoviesResponse> getMovies(int page) {
        if (page < MIN_PAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PAGE_MIN_MESSAGE);
        }

        Pageable pageable = PageRequest.of(page - MIN_PAGE, MOVIE_PAGE_SIZE,
                Sort.by(Sort.Order.asc("ranking"), Sort.Order.asc("id"))
        );
        return movieRepository.findAll(pageable).map(MoviesResponse::from);
    }

    public List<MoviePreviewResponse> getMoviePreviews() {
        return movieRepository
                .findTop5ByOrderByReleaseDateDescIdDesc()
                .stream()
                .map(MoviePreviewResponse::from)
                .toList();
    }

    public MovieResponse getMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "영화를 찾을 수 없습니다."
                ));
        return MovieResponse.from(movie);
    }

    public Page<MoviesResponse> searchMovies(int page, String keyword) {
        if (page < MIN_PAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PAGE_MIN_MESSAGE);
        }

        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "검색어를 입력해야 합니다."
            );
        }

        Pageable pageable = PageRequest.of(page - MIN_PAGE, MOVIE_PAGE_SIZE,
                Sort.by(
                        Sort.Order.asc("ranking"),
                        Sort.Order.asc("id")
                )
        );

        return movieRepository.findByTitleContaining(keyword.trim(), pageable)
                .map(MoviesResponse::from);
    }
}