-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    mobile_no VARCHAR(20),
    date_of_birth VARCHAR(20),
    address TEXT,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create assets table
CREATE TABLE IF NOT EXISTS assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create gold_rates table
CREATE TABLE IF NOT EXISTS gold_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE UNIQUE NOT NULL,
    gold_22k DOUBLE NOT NULL,
    gold_24k DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create gold_predictions table
CREATE TABLE IF NOT EXISTS gold_predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prediction_date DATE NOT NULL,
    predicted_22k DOUBLE NOT NULL,
    predicted_24k DOUBLE NOT NULL,
    confidence INTEGER NOT NULL,
    trend VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_gold_rates_date ON gold_rates(date);
CREATE INDEX IF NOT EXISTS idx_gold_predictions_date ON gold_predictions(prediction_date);
CREATE INDEX IF NOT EXISTS idx_assets_name ON assets(name);
