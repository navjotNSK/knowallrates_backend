package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.AddressRequest;
import com.knowallrates.goldapi.dto.AddressResponse;
import com.knowallrates.goldapi.model.User;
import com.knowallrates.goldapi.service.AddressService;
import com.knowallrates.goldapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            List<AddressResponse> addresses = addressService.getUserAddresses(user);
//            return ResponseEntity.ok(addresses);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(addresses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createAddress(@Valid @RequestBody AddressRequest request,
                                         Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            AddressResponse address = addressService.createAddress(user, request);
//            return ResponseEntity.status(HttpStatus.CREATED).body(address);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            AddressResponse address = addressService.getAddressById(user, id);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, 
                                         @Valid @RequestBody AddressRequest request,
                                         Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            AddressResponse address = addressService.updateAddress(user, id, request);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            addressService.deleteAddress(user, id);
            return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/default")
    public ResponseEntity<AddressResponse> getDefaultAddress(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            AddressResponse defaultAddress = addressService.getDefaultAddress(user);
            if (defaultAddress != null) {
//                return ResponseEntity.ok(defaultAddress);
                return ResponseEntity.ok()
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                        .header("Access-Control-Allow-Headers", "*")
                        .body(defaultAddress);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
