package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.CartResponse;
import com.knowallrates.goldapi.model.*;
import com.knowallrates.goldapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public CartResponse getCart(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        
        Cart cart;
        if (cartOpt.isEmpty()) {
            cart = new Cart(user);
            cart = cartRepository.save(cart);
        } else {
            cart = cartOpt.get();
        }

        return new CartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(String userEmail, Long productId, Integer quantity) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        User user = userOpt.get();
        Product product = productOpt.get();

        // Check stock
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        // Get or create cart
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        Cart cart;
        if (cartOpt.isEmpty()) {
            cart = new Cart(user);
            cart = cartRepository.save(cart);
        } else {
            cart = cartOpt.get();
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItemOpt.isPresent()) {
            // Update existing item
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem(cart, product, quantity);
            cartItemRepository.save(cartItem);
        }

        // Update cart total
        updateCartTotal(cart);

        return new CartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(String userEmail, Long cartItemId, Integer quantity) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem cartItem = cartItemOpt.get();
        
        // Verify ownership
        if (!cartItem.getCart().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            // Check stock
            if (cartItem.getProduct().getStockQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }
            
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        // Update cart total
        updateCartTotal(cartItem.getCart());

        return new CartResponse(cartItem.getCart());
    }

    @Transactional
    public void removeFromCart(String userEmail, Long cartItemId) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem cartItem = cartItemOpt.get();
        
        // Verify ownership
        if (!cartItem.getCart().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        
        // Update cart total
        updateCartTotal(cart);
    }

    @Transactional
    public void clearCart(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<Cart> cartOpt = cartRepository.findByUser(userOpt.get());
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartItemRepository.deleteByCart(cart);
            cart.setTotalAmount(0.0);
            cartRepository.save(cart);
        }
    }

    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }
}
