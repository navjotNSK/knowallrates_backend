package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.UpdateRateRequest;
import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.GoldRate;
import com.knowallrates.goldapi.repository.AssetRepository;
import com.knowallrates.goldapi.repository.GoldRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private GoldRateRepository goldRateRepository;
    
    @Autowired
    private AssetRepository assetRepository;
    
    public List<Asset> getAvailableAssets() {
        return assetRepository.findByIsActive(true);
    }
    
    public GoldRate updateTodayRate(UpdateRateRequest request) {
        LocalDate date = request.getDate() != null ? 
            LocalDate.parse(request.getDate()) : LocalDate.now();
        
        // Check if asset exists
        Optional<Asset> assetOpt = assetRepository.findByName(request.getAssetName());
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found: " + request.getAssetName());
        }
        
        // Find existing rate for the date or create new one
        Optional<GoldRate> existingRateOpt = goldRateRepository.findByDate(date);
        GoldRate goldRate;
        
        if (existingRateOpt.isPresent()) {
            goldRate = existingRateOpt.get();
            goldRate.setGold22k(request.getRate22k());
            goldRate.setGold24k(request.getRate24k());
        } else {
            goldRate = new GoldRate(date, request.getRate22k(), request.getRate24k());
        }
        
        return goldRateRepository.save(goldRate);
    }
    
    public void initializeDefaultAssets() {
        if (assetRepository.count() == 0) {
            assetRepository.save(new Asset("gold", "Gold"));
            assetRepository.save(new Asset("silver", "Silver"));
            assetRepository.save(new Asset("bitcoin", "Bitcoin"));
            assetRepository.save(new Asset("altcoin", "Altcoin"));
            System.out.println("Default assets initialized");
        }
    }
}
