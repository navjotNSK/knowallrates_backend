package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByFilename(String filename);
    List<Image> findByOrderByUploadTimeDesc();
}