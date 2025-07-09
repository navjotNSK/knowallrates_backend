package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.HistoryResponse;
import com.knowallrates.goldapi.dto.PredictionResponse;
import com.knowallrates.goldapi.dto.TodayRateResponse;
import com.knowallrates.goldapi.model.GoldPrediction;
import com.knowallrates.goldapi.model.GoldRate;
import com.knowallrates.goldapi.repository.GoldPredictionRepository;
import com.knowallrates.goldapi.repository.GoldRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoldRateService {

    @Autowired
    private GoldRateRepository goldRateRepository;

    @Autowired
    private GoldPredictionRepository goldPredictionRepository;

    public TodayRateResponse getTodayRates() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Get today's rate (or create mock data if not exists)
        Optional<GoldRate> todayRateOpt = goldRateRepository.findByDate(today);
        GoldRate todayRate;

        if (todayRateOpt.isEmpty()) {
            // Create mock data for today
            todayRate = createMockRate(today);
            goldRateRepository.save(todayRate);
        } else {
            todayRate = todayRateOpt.get();
        }

        // Get yesterday's rate (or create mock data if not exists)
        Optional<GoldRate> yesterdayRateOpt = goldRateRepository.findByDate(yesterday);
        GoldRate yesterdayRate;

        if (yesterdayRateOpt.isEmpty()) {
            // Create mock data for yesterday
            yesterdayRate = createMockRate(yesterday);
            goldRateRepository.save(yesterdayRate);
        } else {
            yesterdayRate = yesterdayRateOpt.get();
        }

        // Calculate changes
        double change22k = todayRate.getGold22k() - yesterdayRate.getGold22k();
        double change24k = todayRate.getGold24k() - yesterdayRate.getGold24k();
        double changePercent22k = (change22k / yesterdayRate.getGold22k()) * 100;
        double changePercent24k = (change24k / yesterdayRate.getGold24k()) * 100;

        // Build response
        TodayRateResponse response = new TodayRateResponse();
        response.setDate(todayRate.getDate().toString());
        response.setGold22k(todayRate.getGold22k());
        response.setGold24k(todayRate.getGold24k());
        response.setChange22k(Math.round(change22k * 100.0) / 100.0);
        response.setChange24k(Math.round(change24k * 100.0) / 100.0);
        response.setChangePercent22k(Math.round(changePercent22k * 100.0) / 100.0);
        response.setChangePercent24k(Math.round(changePercent24k * 100.0) / 100.0);
        response.setTimestamp(LocalDateTime.now().toString());

        // Set yesterday's data
        TodayRateResponse.YesterdayRate yesterdayData = new TodayRateResponse.YesterdayRate();
        yesterdayData.setDate(yesterdayRate.getDate().toString());
        yesterdayData.setGold22k(yesterdayRate.getGold22k());
        yesterdayData.setGold24k(yesterdayRate.getGold24k());
        yesterdayData.setTimestamp(yesterdayRate.getUpdatedAt().toString());
        response.setYesterday(yesterdayData);

        return response;
    }

    public HistoryResponse getHistoricalRates(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);

        // Get existing rates
        List<GoldRate> existingRates = goldRateRepository.findRatesFromDate(startDate);

        // Create mock data for missing dates
        for (int i = 0; i < days; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            boolean exists = existingRates.stream()
                    .anyMatch(rate -> rate.getDate().equals(date));

            if (!exists) {
                GoldRate mockRate = createMockRate(date);
                goldRateRepository.save(mockRate);
                existingRates.add(mockRate);
            }
        }

        // Get updated rates and sort
        List<GoldRate> rates = goldRateRepository.findRatesFromDate(startDate);
        rates.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        // Convert to response format
        List<HistoryResponse.HistoryRate> historyRates = rates.stream()
                .map(rate -> new HistoryResponse.HistoryRate(
                        rate.getDate().toString(),
                        rate.getGold22k(),
                        rate.getGold24k()
                ))
                .collect(Collectors.toList());

        return new HistoryResponse(historyRates);
    }

    public PredictionResponse getPrediction() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Get existing prediction or create new one
        Optional<GoldPrediction> predictionOpt = goldPredictionRepository.findByPredictionDate(tomorrow);
        GoldPrediction prediction;

        if (predictionOpt.isEmpty()) {
            // Create mock prediction
            prediction = createMockPrediction(tomorrow);
            goldPredictionRepository.save(prediction);
        } else {
            prediction = predictionOpt.get();
        }

        return new PredictionResponse(
                prediction.getPredictionDate().toString(),
                prediction.getPredicted22k(),
                prediction.getPredicted24k(),
                prediction.getConfidence(),
                prediction.getTrend().toString().toLowerCase()
        );
    }

    private GoldRate createMockRate(LocalDate date) {
        // Base rates with some realistic variation
        double base22k = 5800 + (Math.random() * 100); // 5800-5900 range
        double base24k = 6300 + (Math.random() * 100); // 6300-6400 range

        // Add some date-based variation to make it more realistic
        long daysSinceEpoch = date.toEpochDay();
        double variation = Math.sin(daysSinceEpoch * 0.1) * 50;

        return new GoldRate(
                date,
                Math.round((base22k + variation) * 100.0) / 100.0,
                Math.round((base24k + variation) * 100.0) / 100.0
        );
    }

    private GoldPrediction createMockPrediction(LocalDate date) {
        // Get today's rate for prediction base
        LocalDate today = LocalDate.now();
        Optional<GoldRate> todayRateOpt = goldRateRepository.findByDate(today);

        double predicted22k = 5850;
        double predicted24k = 6380;

        if (todayRateOpt.isPresent()) {
            GoldRate todayRate = todayRateOpt.get();
            // Predict slight increase/decrease based on random market conditions
            double change = (Math.random() - 0.5) * 100; // -50 to +50 change
            predicted22k = todayRate.getGold22k() + change;
            predicted24k = todayRate.getGold24k() + change;
        }

        // Determine trend
        GoldPrediction.Trend trend;
        if (Math.random() > 0.6) {
            trend = GoldPrediction.Trend.UP;
        } else if (Math.random() > 0.3) {
            trend = GoldPrediction.Trend.DOWN;
        } else {
            trend = GoldPrediction.Trend.STABLE;
        }

        // Random confidence between 70-95%
        int confidence = 70 + (int) (Math.random() * 25);

        return new GoldPrediction(
                date,
                Math.round(predicted22k * 100.0) / 100.0,
                Math.round(predicted24k * 100.0) / 100.0,
                confidence,
                trend
        );
    }
}