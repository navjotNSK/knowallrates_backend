package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.HistoryResponse;
import com.knowallrates.goldapi.dto.PredictionResponse;
import com.knowallrates.goldapi.dto.TodayRateResponse;
import com.knowallrates.goldapi.service.GoldRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate")
@CrossOrigin(origins = {"http://localhost:3000", "https://your-frontend-domain.com" , "https://know-all-rates.up.railway.app/"})
public class GoldRateController {

    @Autowired
    private GoldRateService goldRateService;

    @GetMapping("/today")
    public ResponseEntity<TodayRateResponse> getTodayRates() {
        try {
            TodayRateResponse response = goldRateService.getTodayRates();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<HistoryResponse> getHistoricalRates(
            @RequestParam(defaultValue = "10") int days) {
        try {
            if (days < 1 || days > 365) {
                return ResponseEntity.badRequest().build();
            }

            HistoryResponse response = goldRateService.getHistoricalRates(days);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/predict")
    public ResponseEntity<PredictionResponse> getPrediction() {
        try {
            PredictionResponse response = goldRateService.getPrediction();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Gold Rates API is running!");
    }
}