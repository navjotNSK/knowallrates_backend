-- Insert default assets (only if not exists)
INSERT INTO assets (name, display_name, is_active, created_at)
SELECT name, display_name, is_active, created_at
FROM (
    SELECT 'gold' as name, 'Gold' as display_name, true as is_active, CURRENT_TIMESTAMP as created_at
    UNION ALL
    SELECT 'silver', 'Silver', true, CURRENT_TIMESTAMP
    UNION ALL
    SELECT 'bitcoin', 'Bitcoin', true, CURRENT_TIMESTAMP
    UNION ALL
    SELECT 'altcoin', 'Altcoin', true, CURRENT_TIMESTAMP
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM assets WHERE assets.name = tmp.name
);


-- Insert sample gold rates data (only if not exists)
INSERT INTO gold_rates (date, gold_22k, gold_24k, created_at, updated_at)
SELECT date, gold_22k, gold_24k, created_at, updated_at
FROM (
    SELECT DATE '2024-01-01' as date, 5800.00 as gold_22k, 6300.00 as gold_24k, CURRENT_TIMESTAMP as created_at, CURRENT_TIMESTAMP as updated_at
    UNION ALL
    SELECT DATE '2024-01-02', 5825.00, 6325.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT DATE '2024-01-03', 5850.00, 6350.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT DATE '2024-01-04', 5875.00, 6375.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT DATE '2024-01-05', 5900.00, 6400.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '10 days', 5750.00, 6250.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '9 days', 5775.00, 6275.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '8 days', 5800.00, 6300.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '7 days', 5825.00, 6325.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '6 days', 5850.00, 6350.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '5 days', 5875.00, 6375.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '4 days', 5900.00, 6400.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '3 days', 5925.00, 6425.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '2 days', 5950.00, 6450.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE - INTERVAL '1 day', 5975.00, 6475.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    UNION ALL
    SELECT CURRENT_DATE, 6000.00, 6500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) AS tmp (date, gold_22k, gold_24k, created_at, updated_at)
WHERE NOT EXISTS (
    SELECT 1 FROM gold_rates WHERE gold_rates.date = tmp.date
);


-- Insert sample predictions (only if not exists)
INSERT INTO gold_predictions (prediction_date, predicted_22k, predicted_24k, confidence, trend, created_at)
SELECT prediction_date, predicted_22k, predicted_24k, confidence, trend, created_at
FROM (
    SELECT CURRENT_DATE + INTERVAL '1 day' as prediction_date, 6025.00 as predicted_22k, 6525.00 as predicted_24k, 85 as confidence, 'UP' as trend, CURRENT_TIMESTAMP as created_at
    UNION ALL
    SELECT CURRENT_DATE + INTERVAL '2 days', 6050.00, 6550.00, 78, 'UP', CURRENT_TIMESTAMP
) AS tmp (prediction_date, predicted_22k, predicted_24k, confidence, trend, created_at)
WHERE NOT EXISTS (
    SELECT 1 FROM gold_predictions WHERE gold_predictions.prediction_date = tmp.prediction_date
);