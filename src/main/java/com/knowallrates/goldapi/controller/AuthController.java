package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.AuthRequest;
import com.knowallrates.goldapi.dto.AuthResponse;
import com.knowallrates.goldapi.dto.SignUpRequest;
import com.knowallrates.goldapi.dto.UpdateProfileRequest;
import com.knowallrates.goldapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            System.out.println("POST /api/auth/signup - Request received for email: " + request.getEmail());
            AuthResponse response = userService.signUp(request);
            System.out.println("POST /api/auth/signup - User created successfully with token: " +
                    response.getToken().substring(0, Math.min(20, response.getToken().length())) + "...");
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Error in signUp: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(new AuthResponse()); // Return empty response on error
        }
    }

    @PostMapping("/signin")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody AuthRequest request) {
        try {
            System.out.println("POST /api/auth/signin - Request received for email: " + request.getEmail());
            AuthResponse response = userService.signIn(request);
            System.out.println("POST /api/auth/signin - User signed in successfully");
            System.out.println("Generated token: " + response.getToken().substring(0, Math.min(50, response.getToken().length())) + "...");
            System.out.println("User role: " + response.getUser().getRole());

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Error in signIn: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .header("Access-Control-Allow-Origin", "*")
                    .body(new AuthResponse()); // Return empty response on error
        }
    }

    @GetMapping("/profile")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponse.UserProfile> getProfile(Authentication authentication) {
        try {
            System.out.println("GET /api/auth/profile - Request received");
            if (authentication == null || authentication.getName() == null) {
                System.err.println("GET /api/auth/profile - No authentication found");
                return ResponseEntity.status(401).build();
            }

            String email = authentication.getName();
            System.out.println("GET /api/auth/profile - Getting profile for: " + email);
            AuthResponse.UserProfile profile = userService.getProfile(email);
            System.out.println("GET /api/auth/profile - Profile retrieved successfully");

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(profile);
        } catch (Exception e) {
            System.err.println("Error in getProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }

    @PutMapping("/profile")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponse.UserProfile> updateProfile(
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest,
            Authentication authentication) {
        try {
            System.out.println("PUT /api/auth/profile - Request received");
            if (authentication == null || authentication.getName() == null) {
                System.err.println("PUT /api/auth/profile - No authentication found");
                return ResponseEntity.status(401).build();
            }

            String email = authentication.getName();
            System.out.println("PUT /api/auth/profile - Updating profile for: " + email);
            AuthResponse.UserProfile profile = userService.updateProfile(email, updateProfileRequest);
            System.out.println("PUT /api/auth/profile - Profile updated successfully");

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(profile);
        } catch (Exception e) {
            System.err.println("Error in updateProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }

    @PostMapping("/signout")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> signOut() {
        System.out.println("POST /api/auth/signout - Request received");
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body("Signed out successfully");
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
