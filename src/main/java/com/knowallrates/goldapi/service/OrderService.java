package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.OrderRequest;
import com.knowallrates.goldapi.dto.OrderResponse;
import com.knowallrates.goldapi.dto.OrderItemResponse;
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
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    @Autowired
    private EmailService emailService;

    @Transactional
    public OrderResponse createOrder(String userEmail, OrderRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (cartOpt.isEmpty()) {
            throw new RuntimeException("Cart not found");
        }

        Cart cart = cartOpt.get();
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create a copy of cart items data BEFORE any database operations
        List<CartItemData> cartItemsData = new ArrayList<>();
        for (CartItem item : cartItems) {
            // Validate stock
            if (item.getProduct().getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProduct().getName());
            }

            // Store cart item data
            CartItemData itemData = new CartItemData();
            itemData.productId = item.getProduct().getId();
            itemData.productName = item.getProduct().getName();
            itemData.quantity = item.getQuantity();
            itemData.unitPrice = item.getUnitPrice();
            itemData.totalPrice = item.getTotalPrice();
            cartItemsData.add(itemData);
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

        // Save order first
        order = orderRepository.save(order);

        // Create order items using the copied data (not the CartItem entities)
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemData itemData : cartItemsData) {
            // Get fresh product reference
            Optional<Product> productOpt = productRepository.findById(itemData.productId);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Product not found: " + itemData.productId);
            }

            Product product = productOpt.get();

            // Create OrderItem without referencing CartItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemData.quantity);
            orderItem.setUnitPrice(itemData.unitPrice);
            orderItem.setTotalPrice(itemData.totalPrice);
            orderItems.add(orderItem);

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - itemData.quantity);
            productRepository.save(product);
        }

        // Set order items
        order.setItems(orderItems);

        // NOW delete cart items - this won't affect OrderItems since we didn't reference CartItems
        cartItemRepository.deleteByCart(cart);
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);

        // Send order confirmation email
        try {
            emailService.sendOrderConfirmationEmail(user.getEmail(), user.getFullName(), order, cartItemsData);
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
            // Don't fail the order creation if email fails
        }

        // Convert to DTO to avoid circular references
        return convertToOrderResponse(order);
    }

    // Inner class to hold cart item data
    static class CartItemData {
        Long productId;
        String productName;
        Integer quantity;
        Double unitPrice;
        Double totalPrice;
    }

    public List<OrderResponse> getUserOrders(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    public Page<OrderResponse> getUserOrdersPaginated(String userEmail, Pageable pageable) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Page<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(userOpt.get(), pageable);
        return orders.map(this::convertToOrderResponse);
    }

    public Optional<OrderResponse> getOrderById(String orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        return orderOpt.map(this::convertToOrderResponse);
    }

    public Optional<OrderResponse> getUserOrder(String userEmail, String orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isPresent() && orderOpt.get().getUser().getEmail().equals(userEmail)) {
            return Optional.of(convertToOrderResponse(orderOpt.get()));
        }
        return Optional.empty();
    }

    @Transactional
    public OrderResponse updateOrderStatus(String orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        order = orderRepository.save(order);
        return convertToOrderResponse(order);
    }

    @Transactional
    public OrderResponse updatePaymentStatus(String orderId, Order.PaymentStatus paymentStatus, String transactionId) {
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

        order = orderRepository.save(order);
        return convertToOrderResponse(order);
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

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse(order);

        if (order.getItems() != null) {
            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(OrderItemResponse::new)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }
}
