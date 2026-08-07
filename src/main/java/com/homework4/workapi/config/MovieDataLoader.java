package com.homework4.workapi.config;

import com.homework4.workapi.entity.Movie;
import com.homework4.workapi.repository.MovieRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Profile("movie-import")
@RequiredArgsConstructor
public class MovieDataLoader
        implements ApplicationRunner {

    private static final int BATCH_SIZE = 100;

    private final ObjectMapper objectMapper;
    private final MovieRepository movieRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args)
            throws Exception {

        String dataPath = System.getenv("MOVIE_DATA_PATH");

        if (dataPath == null || dataPath.isBlank()) {
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

        List<MovieData> movieData = objectMapper.readValue(
                path,
                new TypeReference<>() {}
        );

        int insertedCount = 0;
        int skippedExistingCount = 0;

        for (
                int start = 0;
                start < movieData.size();
                start += BATCH_SIZE
        ) {
            int end = Math.min(
                    start + BATCH_SIZE,
                    movieData.size()
            );

            List<MovieData> batch =
                    movieData.subList(start, end);

            List<Long> tmdbIds = batch.stream()
                    .map(MovieData::tmdbId)
                    .toList();

            Set<Long> existingTmdbIds = new HashSet<>(
                    movieRepository.findExistingTmdbIds(
                            tmdbIds
                    )
            );

            List<Movie> newMovies = batch.stream()
                    .filter(data ->
                            !existingTmdbIds.contains(
                                    data.tmdbId()
                            )
                    )
                    .map(MovieData::toEntity)
                    .toList();

            if (!newMovies.isEmpty()) {
                movieRepository.saveAll(newMovies);
                movieRepository.flush();
                entityManager.clear();
            }

            insertedCount += newMovies.size();
            skippedExistingCount +=
                    batch.size() - newMovies.size();

            log.info(
                    "영화 적재 진행: {}/{} "
                            + "(신규: {}, 기존 건너뜀: {})",
                    end,
                    movieData.size(),
                    insertedCount,
                    skippedExistingCount
            );
        }

        log.info(
                "영화 증분 적재 완료: 입력 {}개, 신규 {}개, "
                        + "기존 건너뜀 {}개",
                movieData.size(),
                insertedCount,
                skippedExistingCount
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