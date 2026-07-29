package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findTop4ByOrderByReleaseDateDescIdDesc();
}
