package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.AllRatesResponse;
import com.knowallrates.goldapi.dto.HistoryResponse;
import com.knowallrates.goldapi.dto.TodayRateResponse;
import com.knowallrates.goldapi.model.Asset;
import com.knowallrates.goldapi.model.AssetRate;
import com.knowallrates.goldapi.repository.AssetRepository;
import com.knowallrates.goldapi.repository.AssetRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetRateService {

    @Autowired
    private AssetRateRepository assetRateRepository;

    @Autowired
    private AssetRepository assetRepository;

    public AllRatesResponse getAllTodayRates() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<Asset> activeAssets = assetRepository.findByIsActive(true);
        List<AllRatesResponse.AssetRateInfo> rateInfos = new ArrayList<>();

        for (Asset asset : activeAssets) {
            // Get today's rate
            Optional<AssetRate> todayRateOpt = assetRateRepository.findByAssetAndDate(asset, today);
            AssetRate todayRate;

            if (todayRateOpt.isEmpty()) {
                todayRate = createMockRate(asset, today);
                assetRateRepository.save(todayRate);
            } else {
                todayRate = todayRateOpt.get();
            }

            // Get yesterday's rate for comparison
            Optional<AssetRate> yesterdayRateOpt = assetRateRepository.findByAssetAndDate(asset, yesterday);
            AssetRate yesterdayRate;

            if (yesterdayRateOpt.isEmpty()) {
                yesterdayRate = createMockRate(asset, yesterday);
                assetRateRepository.save(yesterdayRate);
            } else {
                yesterdayRate = yesterdayRateOpt.get();
            }

            // Create rate info
            AllRatesResponse.AssetRateInfo rateInfo;
            if (asset.getName().equals("gold")) {
                rateInfo = new AllRatesResponse.AssetRateInfo(
                    asset.getName(), asset.getDisplayName(),
                    todayRate.getRate22k(), todayRate.getRate24k()
                );
                
                // Calculate change for 22k (primary rate for gold)
                if (todayRate.getRate22k() != null && yesterdayRate.getRate22k() != null) {
                    double change = todayRate.getRate22k() - yesterdayRate.getRate22k();
                    double changePercent = (change / yesterdayRate.getRate22k()) * 100;
                    rateInfo.setChange(Math.round(change * 100.0) / 100.0);
                    rateInfo.setChangePercent(Math.round(changePercent * 100.0) / 100.0);
                    rateInfo.setTrend(change > 0 ? "up" : change < 0 ? "down" : "stable");
                }
            } else {
                rateInfo = new AllRatesResponse.AssetRateInfo(
                    asset.getName(), asset.getDisplayName(),
                    todayRate.getRatePerUnit(), todayRate.getUnit()
                );
                
                // Calculate change for per unit rate
                if (todayRate.getRatePerUnit() != null && yesterdayRate.getRatePerUnit() != null) {
                    double change = todayRate.getRatePerUnit() - yesterdayRate.getRatePerUnit();
                    double changePercent = (change / yesterdayRate.getRatePerUnit()) * 100;
                    rateInfo.setChange(Math.round(change * 100.0) / 100.0);
                    rateInfo.setChangePercent(Math.round(changePercent * 100.0) / 100.0);
                    rateInfo.setTrend(change > 0 ? "up" : change < 0 ? "down" : "stable");
                }
            }

            rateInfos.add(rateInfo);
        }

        return new AllRatesResponse(today.toString(), rateInfos);
    }

    public HistoryResponse getAssetHistory(String assetName, int days) {
        Optional<Asset> assetOpt = assetRepository.findByName(assetName);
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found: " + assetName);
        }

        Asset asset = assetOpt.get();
        LocalDate startDate = LocalDate.now().minusDays(days - 1);

        // Get existing rates
        List<AssetRate> existingRates = assetRateRepository.findByAssetAndDateGreaterThanEqualOrderByDateDesc(asset, startDate);

        // Create mock data for missing dates
        for (int i = 0; i < days; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            boolean exists = existingRates.stream()
                    .anyMatch(rate -> rate.getDate().equals(date));

            if (!exists) {
                AssetRate mockRate = createMockRate(asset, date);
                assetRateRepository.save(mockRate);
                existingRates.add(mockRate);
            }
        }

        // Get updated rates and sort
        List<AssetRate> rates = assetRateRepository.findByAssetAndDateGreaterThanEqualOrderByDateDesc(asset, startDate);
        rates.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        // Convert to response format
        List<HistoryResponse.HistoryRate> historyRates = rates.stream()
                .map(rate -> {
                    if (asset.getName().equals("gold")) {
                        return new HistoryResponse.HistoryRate(
                                rate.getDate().toString(),
                                rate.getRate22k(),
                                rate.getRate24k()
                        );
                    } else {
                        return new HistoryResponse.HistoryRate(
                                rate.getDate().toString(),
                                rate.getRatePerUnit(),
                                null
                        );
                    }
                })
                .collect(Collectors.toList());

        return new HistoryResponse(historyRates);
    }

    private AssetRate createMockRate(Asset asset, LocalDate date) {
        switch (asset.getName().toLowerCase()) {
            case "gold":
                double base22k = 5800 + (Math.random() * 100);
                double base24k = 6300 + (Math.random() * 100);
                long daysSinceEpoch = date.toEpochDay();
                double variation = Math.sin(daysSinceEpoch * 0.1) * 50;
                return new AssetRate(
                    asset, date,
                    Math.round((base22k + variation) * 100.0) / 100.0,
                    Math.round((base24k + variation) * 100.0) / 100.0
                );
                
            case "silver":
                double silverBase = 70 + (Math.random() * 10); // 70-80 per gram
                double silverVariation = Math.sin(date.toEpochDay() * 0.15) * 5;
                return new AssetRate(
                    asset, date,
                    Math.round((silverBase + silverVariation) * 100.0) / 100.0,
                    "gram"
                );
                
            case "bitcoin":
                double btcBase = 4000000 + (Math.random() * 1000000); // 40-50 lakh range
                double btcVariation = Math.sin(date.toEpochDay() * 0.2) * 500000;
                return new AssetRate(
                    asset, date,
                    Math.round((btcBase + btcVariation) * 100.0) / 100.0,
                    "coin"
                );
                
            default:
                return new AssetRate(
                    asset, date,
                    100.0 + (Math.random() * 50),
                    "unit"
                );
        }
    }
}
