package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByName(String name);
    List<Asset> findByIsActive(Boolean isActive);
}
