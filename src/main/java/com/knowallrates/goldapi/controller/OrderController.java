package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.OrderRequest;
import com.knowallrates.goldapi.model.Order;
import com.knowallrates.goldapi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            Order order = orderService.createOrder(authentication.getName(), request);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(order);
        } catch (Exception e) {
            System.err.println("Error in createOrder: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<Page<Order>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getUserOrdersPaginated(authentication.getName(), pageable);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(orders);
        } catch (Exception e) {
            System.err.println("Error in getUserOrders: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{orderId}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Order> getOrder(
            @PathVariable String orderId,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            Optional<Order> order = orderService.getUserOrder(authentication.getName(), orderId);
            if (order.isPresent()) {
                return ResponseEntity.ok()
                        .header("Access-Control-Allow-Origin", "*")
                        .body(order.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error in getOrder: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/validate-coupon")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @RequestParam String couponCode,
            @RequestParam double orderAmount) {
        try {
            double discount = orderService.validateCoupon(couponCode, orderAmount);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(Map.of(
                        "valid", true,
                        "discount", discount,
                        "message", "Coupon applied successfully"
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(Map.of(
                        "valid", false,
                        "discount", 0.0,
                        "message", e.getMessage()
                    ));
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam Order.OrderStatus status) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(order);
        } catch (Exception e) {
            System.err.println("Error in updateOrderStatus: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{orderId}/payment")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Order> updatePaymentStatus(
            @PathVariable String orderId,
            @RequestParam Order.PaymentStatus paymentStatus,
            @RequestParam(required = false) String transactionId) {
        try {
            Order order = orderService.updatePaymentStatus(orderId, paymentStatus, transactionId);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(order);
        } catch (Exception e) {
            System.err.println("Error in updatePaymentStatus: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Handle preflight requests
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }
}
