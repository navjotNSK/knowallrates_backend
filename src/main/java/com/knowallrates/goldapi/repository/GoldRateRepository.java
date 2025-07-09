package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.GoldRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoldRateRepository extends JpaRepository<GoldRate, Long> {

    Optional<GoldRate> findByDate(LocalDate date);

    @Query("SELECT gr FROM GoldRate gr WHERE gr.date >= :startDate ORDER BY gr.date DESC")
    List<GoldRate> findRatesFromDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT gr FROM GoldRate gr ORDER BY gr.date DESC LIMIT 2")
    List<GoldRate> findLatestTwoRates();

    @Query("SELECT gr FROM GoldRate gr ORDER BY gr.date DESC LIMIT :limit")
    List<GoldRate> findLatestRates(@Param("limit") int limit);
}