package com.knowallrates.goldapi.repository;

import com.knowallrates.goldapi.model.Cart;
import com.knowallrates.goldapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
