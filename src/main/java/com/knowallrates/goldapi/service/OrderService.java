package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.OrderRequest;
import com.knowallrates.goldapi.model.*;
import com.knowallrates.goldapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(String userEmail, OrderRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Cart cart = cartOpt.get();
        
        // Validate stock for all items
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProduct().getName());
            }
        }

        // Calculate totals
        double subtotal = cart.getTotalAmount();
        double discountAmount = 0.0;
        double taxAmount = subtotal * 0.18; // 18% GST
        double shippingAmount = subtotal > 50000 ? 0.0 : 500.0; // Free shipping above 50k

        // Apply coupon if provided
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCodeAndIsActiveTrue(request.getCouponCode());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (isValidCoupon(coupon, subtotal)) {
                    discountAmount = calculateDiscount(coupon, subtotal);
                    
                    // Update coupon usage
                    coupon.setUsedCount(coupon.getUsedCount() + 1);
                    couponRepository.save(coupon);
                }
            }
        }

        double totalAmount = subtotal - discountAmount + taxAmount + shippingAmount;

        // Create order
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(orderId, user, subtotal, totalAmount, request.getShippingAddress());
        order.setDiscountAmount(discountAmount);
        order.setTaxAmount(taxAmount);
        order.setShippingAmount(shippingAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setCouponCode(request.getCouponCode());
        order.setOrderNotes(request.getOrderNotes());

        order = orderRepository.save(order);

        // Create order items and update stock
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                order, 
                cartItem.getProduct(), 
                cartItem.getQuantity(), 
                cartItem.getUnitPrice()
            );
            
            // Update product stock
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Clear cart
        cartItemRepository.deleteByCart(cart);
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);

        return order;
    }

    public List<Order> getUserOrders(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return orderRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
    }

    public Page<Order> getUserOrdersPaginated(String userEmail, Pageable pageable) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return orderRepository.findByUserOrderByCreatedAtDesc(userOpt.get(), pageable);
    }

    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public Optional<Order> getUserOrder(String userEmail, String orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isPresent() && orderOpt.get().getUser().getEmail().equals(userEmail)) {
            return orderOpt;
        }
        return Optional.empty();
    }

    @Transactional
    public Order updateOrderStatus(String orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updatePaymentStatus(String orderId, Order.PaymentStatus paymentStatus, String transactionId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Order order = orderOpt.get();
        order.setPaymentStatus(paymentStatus);
        order.setPaymentTransactionId(transactionId);
        
        if (paymentStatus == Order.PaymentStatus.PAID) {
            order.setStatus(Order.OrderStatus.CONFIRMED);
        }
        
        return orderRepository.save(order);
    }

    public double validateCoupon(String couponCode, double orderAmount) {
        Optional<Coupon> couponOpt = couponRepository.findByCodeAndIsActiveTrue(couponCode);
        if (couponOpt.isEmpty()) {
            throw new RuntimeException("Invalid coupon code");
        }

        Coupon coupon = couponOpt.get();
        if (!isValidCoupon(coupon, orderAmount)) {
            throw new RuntimeException("Coupon is not valid for this order");
        }

        return calculateDiscount(coupon, orderAmount);
    }

    private boolean isValidCoupon(Coupon coupon, double orderAmount) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check if coupon is active
        if (!coupon.getIsActive()) {
            return false;
        }
        
        // Check validity period
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return false;
        }
        
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return false;
        }
        
        // Check minimum order amount
        if (orderAmount < coupon.getMinimumOrderAmount()) {
            return false;
        }
        
        // Check usage limit
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return false;
        }
        
        return true;
    }

    private double calculateDiscount(Coupon coupon, double orderAmount) {
        double discount = 0.0;
        
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = orderAmount * (coupon.getDiscountValue() / 100);
        } else {
            discount = coupon.getDiscountValue();
        }
        
        // Apply maximum discount limit if set
        if (coupon.getMaximumDiscountAmount() != null && discount > coupon.getMaximumDiscountAmount()) {
            discount = coupon.getMaximumDiscountAmount();
        }
        
        return Math.round(discount * 100.0) / 100.0;
    }
}
