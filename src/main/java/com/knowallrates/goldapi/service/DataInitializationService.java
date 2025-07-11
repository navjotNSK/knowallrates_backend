package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.model.GoldPrediction;
import com.knowallrates.goldapi.model.GoldRate;
import com.knowallrates.goldapi.model.User;
import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.repository.GoldPredictionRepository;
import com.knowallrates.goldapi.repository.GoldRateRepository;
import com.knowallrates.goldapi.repository.UserRepository;
import com.knowallrates.goldapi.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private GoldRateRepository goldRateRepository;
    
    @Autowired
    private GoldPredictionRepository goldPredictionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeAssets();
        initializeGoldRates();
        initializePredictions();
    }
    
    private void initializeUsers() {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@knowallrates.com")) {
            User admin = new User();
            admin.setEmail("admin@knowallrates.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setMobileNo("9999999999");
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);
            
            userRepository.save(admin);
            System.out.println("✅ Default admin user created: admin@knowallrates.com / admin123");
        } else {
            System.out.println("✅ Admin user already exists");
        }
    }
    
    private void initializeAssets() {
        if (assetRepository.count() == 0) {
            assetRepository.save(new Asset("gold", "Gold"));
            assetRepository.save(new Asset("silver", "Silver"));
            assetRepository.save(new Asset("bitcoin", "Bitcoin"));
            assetRepository.save(new Asset("altcoin", "Altcoin"));
            System.out.println("✅ Default assets initialized");
        } else {
            System.out.println("✅ Assets already exist");
        }
    }
    
    private void initializeGoldRates() {
        // Check if we already have data
        if (goldRateRepository.count() > 0) {
            System.out.println("✅ Gold rates data already exists, skipping initialization");
            return;
        }
        
        System.out.println("Initializing gold rates data...");
        
        // Create sample data for the last 15 days
        LocalDate today = LocalDate.now();
        for (int i = 14; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            double base22k = 5800 + (i * 10) + (Math.random() * 50);
            double base24k = 6300 + (i * 10) + (Math.random() * 50);
            
            GoldRate rate = new GoldRate(
                date,
                Math.round(base22k * 100.0) / 100.0,
                Math.round(base24k * 100.0) / 100.0
            );
            
            goldRateRepository.save(rate);
        }
        
        System.out.println("✅ Gold rates data initialized successfully");
    }
    
    private void initializePredictions() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // Check if prediction already exists
        if (goldPredictionRepository.findByPredictionDate(tomorrow).isPresent()) {
            System.out.println("✅ Prediction data already exists, skipping initialization");
            return;
        }
        
        System.out.println("Initializing prediction data...");
        
        // Create prediction for tomorrow
        GoldPrediction prediction = new GoldPrediction(
            tomorrow,
            6025.0,
            6525.0,
            85,
            GoldPrediction.Trend.UP
        );
        
        goldPredictionRepository.save(prediction);
        
        System.out.println("✅ Prediction data initialized successfully");
    }
}
