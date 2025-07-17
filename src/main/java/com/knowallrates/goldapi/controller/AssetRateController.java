package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.AllRatesResponse;
import com.knowallrates.goldapi.dto.HistoryResponse;
import com.knowallrates.goldapi.service.AssetRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssetRateController {

    @Autowired
    private AssetRateService assetRateService;

    @GetMapping("/all")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AllRatesResponse> getAllRates() {
        try {
            System.out.println("GET /api/rates/all - Request received");
            AllRatesResponse response = assetRateService.getAllTodayRates();
            System.out.println("GET /api/rates/all - Response: " + response.getRates().size() + " rates");
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Error in getAllRates: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{assetName}/history")
    @CrossOrigin(origins = "*")
    public ResponseEntity<HistoryResponse> getAssetHistory(
            @PathVariable String assetName,
            @RequestParam(defaultValue = "10") int days) {
        try {
            System.out.println("GET /api/rates/" + assetName + "/history - Request received with days: " + days);
            if (days < 1 || days > 365) {
                return ResponseEntity.badRequest().build();
            }

            HistoryResponse response = assetRateService.getAssetHistory(assetName, days);
            System.out.println("GET /api/rates/" + assetName + "/history - Response size: " +
                    (response.getRates() != null ? response.getRates().size() : 0));
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(response);
        } catch (Exception e) {
            System.err.println("Error in getAssetHistory: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
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
