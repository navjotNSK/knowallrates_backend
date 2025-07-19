package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.AssetRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRateRepository extends JpaRepository<AssetRate, Long> {
    Optional<AssetRate> findByAssetAndDate(Asset asset, LocalDate date);
    
    @Query("SELECT ar FROM AssetRate ar WHERE ar.asset = :asset AND ar.date >= :startDate ORDER BY ar.date DESC")
    List<AssetRate> findByAssetAndDateGreaterThanEqualOrderByDateDesc(@Param("asset") Asset asset, @Param("startDate") LocalDate startDate);
    
    @Query("SELECT ar FROM AssetRate ar WHERE ar.date = :date")
    List<AssetRate> findAllByDate(@Param("date") LocalDate date);
    
    @Query("SELECT ar FROM AssetRate ar WHERE ar.asset.name = :assetName AND ar.date >= :startDate ORDER BY ar.date DESC")
    List<AssetRate> findByAssetNameAndDateGreaterThanEqual(@Param("assetName") String assetName, @Param("startDate") LocalDate startDate);
    
    Optional<AssetRate> findTopByAssetOrderByDateDesc(Asset asset);
}
