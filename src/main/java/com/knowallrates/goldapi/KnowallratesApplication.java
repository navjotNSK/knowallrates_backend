package com.knowallrates.goldapi;

import com.knowallrates.goldapi.service.AdminService;
import com.knowallrates.goldapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@RestController
public class KnowallratesApplication implements CommandLineRunner  {

	@Autowired
	private UserService userService;

	@Autowired
	private AdminService adminService;

	public static void main(String[] args) {
		SpringApplication.run(KnowallratesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Initialize default data
		userService.createDefaultAdmin();
		adminService.initializeDefaultAssets();
	}

	// Root endpoint for Railway health check
	@GetMapping("/")
	public String home() {
		return "Gold Rates API is running! Visit /api/rate/health for detailed health check.";
	}

	// Simple health endpoint
	@GetMapping("/health")
	public String simpleHealth() {
		return "OK";
	}

}


