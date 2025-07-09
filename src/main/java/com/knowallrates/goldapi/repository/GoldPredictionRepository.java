package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.GoldPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GoldPredictionRepository extends JpaRepository<GoldPrediction, Long> {

    Optional<GoldPrediction> findByPredictionDate(LocalDate predictionDate);

    @Query("SELECT gp FROM GoldPrediction gp WHERE gp.predictionDate >= :date ORDER BY gp.predictionDate ASC LIMIT 1")
    Optional<GoldPrediction> findNextPrediction(@Param("date") LocalDate date);
}