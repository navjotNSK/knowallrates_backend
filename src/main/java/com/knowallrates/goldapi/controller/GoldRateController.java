package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.HistoryResponse;
import com.knowallrates.goldapi.dto.PredictionResponse;
import com.knowallrates.goldapi.dto.TodayRateResponse;
import com.knowallrates.goldapi.dto.TodayRateResponse;
import com.knowallrates.goldapi.service.GoldRateService;
import com.knowallrates.goldapi.service.AssetRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GoldRateController {

    @Autowired
    private GoldRateService goldRateService;

    @Autowired
    private AssetRateService assetRateService;

    @GetMapping("/today")
    @CrossOrigin(origins = "*")
    public ResponseEntity<TodayRateResponse> getTodayRates() {
        try {
            System.out.println("GET /api/rate/today - Request received");
            TodayRateResponse response = goldRateService.getTodayRates();
            System.out.println("GET /api/rate/today - Response: " + response);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Error in getTodayRates: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    @CrossOrigin(origins = "*")
    public ResponseEntity<HistoryResponse> getHistoricalRates(
            @RequestParam(defaultValue = "10") int days) {
        try {
            System.out.println("GET /api/rate/history - Request received with days: " + days);
            if (days < 1 || days > 365) {
                return ResponseEntity.badRequest().build();
            }

            HistoryResponse response = goldRateService.getHistoricalRates(days);
            System.out.println("GET /api/rate/history - Response size: " +
                    (response.getRates() != null ? response.getRates().size() : 0));
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Error in getHistoricalRates: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/predict")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PredictionResponse> getPrediction() {
        try {
            System.out.println("GET /api/rate/predict - Request received");
            PredictionResponse response = goldRateService.getPrediction();
            System.out.println("GET /api/rate/predict - Response: " + response);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Error in getPrediction: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> healthCheck() {
        System.out.println("GET /api/rate/health - Health check requested");
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .body("Gold Rates API is running!");
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
