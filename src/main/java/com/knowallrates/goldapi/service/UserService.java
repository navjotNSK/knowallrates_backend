package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.AuthRequest;
import com.knowallrates.goldapi.dto.AuthResponse;
import com.knowallrates.goldapi.dto.SignUpRequest;
import com.knowallrates.goldapi.dto.UpdateProfileRequest;
import com.knowallrates.goldapi.model.User;
import com.knowallrates.goldapi.repository.UserRepository;
import com.knowallrates.goldapi.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor injection to avoid circular dependency
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signUp(SignUpRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setMobileNo(request.getMobileNo());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        user.setRole(User.Role.USER); // Default role

        user = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());

        return new AuthResponse(token, user);
    }

    public AuthResponse signIn(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActive(request.getEmail(), true);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());

        return new AuthResponse(token, user);
    }

    public AuthResponse.UserProfile getProfile(String email) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActive(email, true);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return new AuthResponse.UserProfile(userOpt.get());
    }

    public AuthResponse.UserProfile updateProfile(String email, UpdateProfileRequest request) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActive(email, true);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        user.setFullName(request.getFullName());
        user.setMobileNo(request.getMobileNo());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());

        user = userRepository.save(user);

        return new AuthResponse.UserProfile(user);
    }

    public void createDefaultAdmin() {
        if (!userRepository.existsByEmail("admin@knowallrates.com")) {
            User admin = new User();
            admin.setEmail("admin@knowallrates.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setMobileNo("9999999999");
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            System.out.println("Default admin user created: admin@knowallrates.com / admin123");
        }
    }
}
