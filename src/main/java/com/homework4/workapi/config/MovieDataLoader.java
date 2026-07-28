package com.homework4.workapi.config;

import com.homework4.workapi.entity.Movie;
import com.homework4.workapi.repository.MovieRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "movie.import.enabled",
        havingValue = "true"
)
public class MovieDataLoader implements ApplicationRunner {

    private static final int BATCH_SIZE = 100;

    private final ObjectMapper objectMapper;
    private final MovieRepository movieRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args)
            throws Exception {

        if (movieRepository.count() > 0) {
            log.info("영화 데이터가 이미 존재하여 적재를 건너뜁니다.");
            return;
        }

        String dataPath = System.getenv(
                "MOVIE_DATA_PATH"
        );

        if (
                dataPath == null
                        || dataPath.isBlank()
        ) {
            throw new IllegalStateException(
                    "MOVIE_DATA_PATH 환경변수가 없습니다."
            );
        }

        Path path = Path.of(dataPath);

        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException(
                    "영화 데이터 파일을 찾을 수 없습니다: "
                            + path
            );
        }

        List<MovieData> movieData =
                objectMapper.readValue(
                        path,
                        new TypeReference<>() {
                        }
                );

        for (
                int start = 0;
                start < movieData.size();
                start += BATCH_SIZE
        ) {
            int end = Math.min(
                    start + BATCH_SIZE,
                    movieData.size()
            );

            List<Movie> movies =
                    movieData.subList(start, end)
                            .stream()
                            .map(MovieData::toEntity)
                            .toList();

            movieRepository.saveAll(movies);
            movieRepository.flush();
            entityManager.clear();

            log.info(
                    "영화 데이터 적재 진행: {}/{}",
                    end,
                    movieData.size()
            );
        }

        log.info(
                "영화 데이터 적재 완료: {}개",
                movieData.size()
        );
    }

    private record MovieData(
            Long tmdbId,
            String title,
            String posterPath,
            int runningTime,
            LocalDate releaseDate,
            String certification,
            String genre,
            String overview,
            String director,
            List<String> actors,
            BigDecimal tmdbRating,
            int ranking
    ) {
        private Movie toEntity() {
            return new Movie(
                    tmdbId,
                    title,
                    posterPath,
                    runningTime,
                    releaseDate,
                    certification,
                    genre,
                    overview,
                    director,
                    tmdbRating,
                    actors,
                    ranking
            );
        }
    }
}