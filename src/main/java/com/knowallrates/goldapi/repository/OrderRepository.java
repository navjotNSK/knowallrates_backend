package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.Order;
import com.knowallrates.goldapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
