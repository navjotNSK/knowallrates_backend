package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeAndIsActiveTrue(String code);
    List<Coupon> findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
        LocalDateTime now1, LocalDateTime now2);
}
