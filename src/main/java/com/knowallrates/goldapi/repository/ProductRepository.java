package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();
    List<Product> findByIsActiveTrueAndIsFeaturedTrue();
    List<Product> findByAssetAndIsActiveTrue(Asset asset);
    List<Product> findByCategoryAndIsActiveTrue(String category);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.asset = :asset")
    Page<Product> findByAsset(@Param("asset") Asset asset, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category = :category")
    Page<Product> findByCategory(@Param("category") String category, Pageable pageable);
}
