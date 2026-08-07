package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findTop5ByOrderByReleaseDateDescIdDesc();


    @Query("""
        select m.tmdbId
        from Movie m
        where m.tmdbId in :tmdbIds
        """)
    List<Long> findExistingTmdbIds(
            @Param("tmdbIds")
            Collection<Long> tmdbIds
    );
}
