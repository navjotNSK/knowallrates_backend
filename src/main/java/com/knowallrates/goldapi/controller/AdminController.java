package com.knowallrates.goldapi.controller;

import com.knowallrates.goldapi.dto.UpdateRateRequest;
import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.GoldRate;
import com.knowallrates.goldapi.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/assets")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<Asset>> getAvailableAssets() {
        try {
            System.out.println("GET /api/admin/assets - Request received");
            List<Asset> assets = adminService.getAvailableAssets();
            System.out.println("GET /api/admin/assets - Found " + assets.size() + " assets");
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(assets);
        } catch (Exception e) {
            System.err.println("Error in getAvailableAssets: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/rates/update")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<GoldRate> updateTodayRate(@Valid @RequestBody UpdateRateRequest request) {
        try {
            System.out.println("POST /api/admin/rates/update - Request received: " + request);
            GoldRate updatedRate = adminService.updateTodayRate(request);
            System.out.println("POST /api/admin/rates/update - Rate updated: " + updatedRate);
            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(updatedRate);
        } catch (Exception e) {
            System.err.println("Error in updateTodayRate: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getAdminDashboard() {
        System.out.println("GET /api/admin/dashboard - Request received");
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body("Admin Dashboard Access Granted");
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
