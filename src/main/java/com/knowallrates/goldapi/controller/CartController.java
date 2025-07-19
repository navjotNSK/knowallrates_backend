package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.CartResponse;
import com.knowallrates.goldapi.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            CartResponse cart = cartService.getCart(authentication.getName());
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(cart);
        } catch (Exception e) {
            System.err.println("Error in getCart: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add")
    @CrossOrigin(origins = "*")
    public ResponseEntity<CartResponse> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = request.containsKey("quantity") ?
                    Integer.valueOf(request.get("quantity").toString()) : 1;

            CartResponse cart = cartService.addToCart(authentication.getName(), productId, quantity);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")

                    .body(cart);
        } catch (Exception e) {
            System.err.println("Error in addToCart: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    @CrossOrigin(origins = "*")
    public ResponseEntity<CartResponse> updateCartItem(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            Long itemId = Long.valueOf(request.get("itemId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());

            CartResponse cart = cartService.updateCartItem(authentication.getName(), itemId, quantity);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(cart);
        } catch (Exception e) {
            System.err.println("Error in updateCartItem: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove/{itemId}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long itemId,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            cartService.removeFromCart(authentication.getName(), itemId);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            System.err.println("Error in removeFromCart: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/clear")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            cartService.clearCart(authentication.getName());
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            System.err.println("Error in clearCart: " + e.getMessage());
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
