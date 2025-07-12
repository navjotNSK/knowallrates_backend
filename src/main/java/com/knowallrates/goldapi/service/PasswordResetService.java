package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.model.PasswordResetToken;
import com.knowallrates.goldapi.model.User;
import com.knowallrates.goldapi.repository.PasswordResetTokenRepository;
import com.knowallrates.goldapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String initiatePasswordReset(String email) {
        System.out.println("=== PASSWORD RESET INITIATED ===");
        System.out.println("Email: " + email);
        System.out.println("Timestamp: " + LocalDateTime.now());
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("User not found for email: " + email);
            // For security, we don't reveal if email exists or not
            return "If an account with this email exists, you will receive a password reset link shortly.";
        }

        User user = userOpt.get();
        System.out.println("Found user: " + user.getEmail() + " (ID: " + user.getId() + ")");

        // Check for recent reset requests (rate limiting)
        Optional<PasswordResetToken> recentToken = tokenRepository.findLatestValidTokenByUserId(user.getId());
        if (recentToken.isPresent()) {
            LocalDateTime recentTokenTime = recentToken.get().getCreatedAt();
            if (recentTokenTime.isAfter(LocalDateTime.now().minusMinutes(5))) {
                System.out.println("Rate limiting: Recent reset request found for user: " + user.getId());
                return "A password reset email was recently sent. Please check your email or wait a few minutes before requesting another.";
            }
        }

        // Invalidate any existing tokens for this user
        tokenRepository.invalidateAllUserTokens(user.getId());
        System.out.println("Invalidated existing tokens for user: " + user.getId());

        // Generate secure token
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // 1 hour expiry

        // Save token
        PasswordResetToken resetToken = new PasswordResetToken(token, user.getId(), expiresAt);
        tokenRepository.save(resetToken);
        System.out.println("Created password reset token for user: " + user.getId());
        System.out.println("Token expires at: " + expiresAt);

        // Send email asynchronously
        sendPasswordResetEmailAsync(user.getEmail(), user.getFullName(), token);

        System.out.println("=== PASSWORD RESET REQUEST COMPLETED ===");
        return "If an account with this email exists, you will receive a password reset link shortly.";
    }

    @Async
    public void sendPasswordResetEmailAsync(String email, String userName, String token) {
        try {
            System.out.println("=== SENDING PASSWORD RESET EMAIL ===");
            System.out.println("To: " + email);
            System.out.println("Token: " + token.substring(0, 10) + "...");
            
            emailService.sendPasswordResetEmail(email, userName, token);
            System.out.println("Password reset email sent successfully to: " + email);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email to: " + email);
            e.printStackTrace();
        }
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {
        System.out.println("=== PASSWORD RESET ATTEMPT ===");
        System.out.println("Token: " + token.substring(0, 10) + "...");
        System.out.println("Timestamp: " + LocalDateTime.now());
        
        // Validate password strength
        if (!isPasswordStrong(newPassword)) {
            System.out.println("Password reset failed: Weak password");
            throw new RuntimeException("Password does not meet security requirements");
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);
        if (tokenOpt.isEmpty()) {
            System.out.println("Invalid or used token: " + token.substring(0, 10) + "...");
            throw new RuntimeException("Invalid or expired reset token");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.isExpired()) {
            System.out.println("Token expired: " + token.substring(0, 10) + "...");
            throw new RuntimeException("Reset token has expired");
        }

        Optional<User> userOpt = userRepository.findById(resetToken.getUserId());
        if (userOpt.isEmpty()) {
            System.out.println("User not found for token: " + token.substring(0, 10) + "...");
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        System.out.println("Resetting password for user: " + user.getEmail());

        // Check if new password is different from current password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            System.out.println("Password reset failed: New password same as current");
            throw new RuntimeException("New password must be different from your current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);

        // Invalidate all other tokens for this user
        tokenRepository.invalidateAllUserTokens(user.getId());

        System.out.println("Password reset successful for user: " + user.getEmail());

        // Send confirmation email asynchronously
        sendPasswordResetConfirmationAsync(user.getEmail(), user.getFullName());

        System.out.println("=== PASSWORD RESET COMPLETED ===");
        return "Password has been reset successfully";
    }

    @Async
    public void sendPasswordResetConfirmationAsync(String email, String userName) {
        try {
            System.out.println("=== SENDING PASSWORD RESET CONFIRMATION ===");
            System.out.println("To: " + email);
            
            emailService.sendPasswordResetConfirmationEmail(email, userName);
            System.out.println("Password reset confirmation email sent to: " + email);
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email to: " + email);
            e.printStackTrace();
        }
    }

    public boolean verifyResetToken(String token) {
        System.out.println("=== TOKEN VERIFICATION ===");
        System.out.println("Token: " + token.substring(0, 10) + "...");
        
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);
        if (tokenOpt.isEmpty()) {
            System.out.println("Token not found or already used: " + token.substring(0, 10) + "...");
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        boolean isValid = resetToken.isValid();
        System.out.println("Token validation result: " + isValid);
        
        if (!isValid) {
            if (resetToken.getUsed()) {
                System.out.println("Token already used");
            } else if (resetToken.isExpired()) {
                System.out.println("Token expired at: " + resetToken.getExpiresAt());
            }
        }
        
        return isValid;
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        System.out.println("Generated secure token: " + token.substring(0, 10) + "...");
        return token;
    }

    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Check for at least one uppercase, one lowercase, and one digit
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        
        return hasUpper && hasLower && hasDigit;
    }

    // Cleanup expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredTokens() {
        System.out.println("=== CLEANING UP EXPIRED TOKENS ===");
        System.out.println("Cleanup time: " + LocalDateTime.now());
        
        try {
            tokenRepository.deleteExpiredTokens(LocalDateTime.now());
            System.out.println("Expired tokens cleanup completed successfully");
        } catch (Exception e) {
            System.err.println("Error during token cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Additional security: Cleanup old tokens daily
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    @Transactional
    public void cleanupOldTokens() {
        System.out.println("=== DAILY TOKEN CLEANUP ===");
        System.out.println("Cleanup time: " + LocalDateTime.now());
        
        try {
            // Delete all tokens older than 7 days
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
            tokenRepository.deleteExpiredTokens(cutoffDate);
            System.out.println("Old tokens cleanup completed successfully");
        } catch (Exception e) {
            System.err.println("Error during old token cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
