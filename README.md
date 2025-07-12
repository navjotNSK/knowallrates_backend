# knowallrates_backend
Backend Spring Boot application for know all rates next application.

# KnowAllRates - Complete Technical Documentation

## ️ System Architecture Overview

### **High-Level Architecture**

```plaintext
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   External      │
│   (Next.js)     │◄──►│   (Spring Boot) │◄──►│   APIs          │
│                 │    │                 │    │                 │
│ • React 18      │    │ • Java 17       │    │ • Metals API    │
│ • TypeScript    │    │ • Spring Boot 3 │    │ • CoinGecko     │
│ • Tailwind CSS  │    │ • Spring Security│   │ • Email SMTP    │
│ • shadcn/ui     │    │ • JWT Auth      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Browser       │    │   Database      │    │   Email Service │
│   Storage       │    │   (H2/MySQL)    │    │   (Gmail SMTP)  │
│                 │    │                 │    │                 │
│ • localStorage  │    │ • User Data     │    │ • Password Reset│
│ • Settings      │    │ • Rates History │    │ • Notifications │
│ • Auth Tokens   │    │ • Reset Tokens  │    │ • Confirmations │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Core Features & Functionality

### **1. Authentication & Authorization System**

#### **Features:**

- ✅ User Registration with validation
- ✅ Secure Login with JWT tokens
- ✅ Password Reset via Email
- ✅ Role-based Access Control (USER/ADMIN)
- ✅ Profile Management
- ✅ Session Management


#### **Security Implementation:**

```java
// JWT Token Structure
{
  "sub": "user@example.com",
  "role": "USER",
  "iat": 1640995200,
  "exp": 1641081600
}
```

#### **Password Reset Flow:**

```plaintext
1. User requests reset → 2. Generate secure token → 3. Send email
                    ↓
6. Confirm success ← 5. Update password ← 4. User clicks link
```

### **2. Real-time Rate Tracking System**

#### **Supported Assets:**

- 🥇 **Gold (22K & 24K)**: Live rates with historical data
- 🥈 **Silver**: Real-time precious metal rates
- ₿ **Bitcoin**: Cryptocurrency tracking
- 💱 **Multi-currency**: INR, USD, EUR, GBP support


#### **Data Sources:**

```javascript
// External API Integration
const dataSources = {
  metals: "https://api.metals.live/v1/spot",
  crypto: "https://api.coingecko.com/api/v3/simple/price",
  fallback: "Mock data with realistic variations"
}
```

### **3. Advanced Settings System**

#### **Real-time UI Features:**

- 🌙 **Dark Mode**: Instant theme switching
- 🌍 **Multi-language**: 10+ languages with real-time switching
- 💰 **Currency**: Live formatting with locale support
- 🕐 **Timezone**: Real-time date/time display
- 🔔 **Notifications**: Push notification preferences


#### **Settings Architecture:**

```typescript
interface Settings {
  language: string    // Real-time UI translation
  darkMode: boolean   // Instant theme application
  currency: string    // Live price formatting
  timezone: string    // Real-time date display
  notifications: boolean
}
```

### **4. Admin Panel & Management**

#### **Admin Features:**

- 📊 Rate Management Dashboard
- 👥 User Management
- 📈 Analytics & Reporting
- ⚙️ System Configuration
- 🔐 Secure Admin Access


### **5. Payment Integration**

#### **Payment Methods:**

- 💳 **UPI Payments**: QR codes and UPI links
- 💰 **Card Payments**: Razorpay integration
- 📱 **Mobile Payments**: GPay, PhonePe support
- 🔒 **Secure Processing**: PCI compliant


## ️ Technical Stack Details

### **Frontend Architecture**

#### **Core Technologies:**

```json
{
  "framework": "Next.js 14 (App Router)",
  "language": "TypeScript",
  "styling": "Tailwind CSS + shadcn/ui",
  "state": "React Context + localStorage",
  "charts": "Recharts",
  "icons": "Lucide React"
}
```

#### **Key Components:**

```plaintext
src/
├── app/
│   ├── (auth)/
│   │   ├── signin/
│   │   ├── signup/
│   │   ├── forgot-password/
│   │   └── reset-password/
│   ├── admin/
│   ├── settings/
│   ├── profile/
│   ├── payment/
│   └── api/
├── components/ui/
├── lib/
│   ├── auth.ts
│   ├── settings-context.tsx
│   └── utils.ts
└── styles/
```

### **Backend Architecture**

#### **Core Technologies:**

```java
// Technology Stack
Spring Boot 3.2+
Java 17+
Spring Security 6
JWT Authentication
H2/MySQL Database
JavaMail API
Async Processing
Scheduled Tasks
```

#### **Package Structure:**

```plaintext
com.knowallrates.goldapi/
├── config/
│   ├── SecurityConfig.java
│   ├── AsyncConfig.java
│   └── CorsConfig.java
├── controller/
│   ├── AuthController.java
│   ├── RateController.java
│   ├── AdminController.java
│   ├── SettingsController.java
│   └── PasswordResetController.java
├── service/
│   ├── AuthService.java
│   ├── RateService.java
│   ├── ExternalApiService.java
│   ├── EmailService.java
│   ├── PasswordResetService.java
│   └── SettingsService.java
├── model/
│   ├── User.java
│   ├── GoldRate.java
│   ├── Asset.java
│   ├── UserSettings.java
│   └── PasswordResetToken.java
├── repository/
├── dto/
├── security/
└── GoldapiApplication.java
```

## Security Implementation

### **Authentication Flow:**

```plaintext
1. User Login → 2. Validate Credentials → 3. Generate JWT
                                    ↓
6. Access Resources ← 5. Validate Token ← 4. Store Token
```

### **Password Reset Security:**

```java
// Secure Token Generation
private String generateSecureToken() {
    byte[] tokenBytes = new byte[32]; // 256 bits
    secureRandom.nextBytes(tokenBytes);
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(tokenBytes);
}

// Token Validation
public boolean isValid() {
    return !this.used && 
           !LocalDateTime.now().isAfter(this.expiresAt);
}
```

### **Security Features:**

- 🔒 **JWT Authentication**: Stateless token-based auth
- 🛡️ **CORS Protection**: Configured cross-origin policies
- 🔐 **Password Encryption**: BCrypt hashing
- ⏰ **Token Expiration**: 1-hour reset token validity
- 🚫 **Rate Limiting**: Protection against brute force
- 📧 **Email Verification**: Secure password reset flow


## Database Schema

### **Core Tables:**

#### **Users Table:**

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    mobile_no VARCHAR(20),
    date_of_birth DATE,
    address TEXT,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### **Password Reset Tokens:**

```sql
CREATE TABLE password_reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### **User Settings:**

```sql
CREATE TABLE user_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    language VARCHAR(10) DEFAULT 'en',
    dark_mode BOOLEAN DEFAULT FALSE,
    notifications BOOLEAN DEFAULT TRUE,
    currency VARCHAR(10) DEFAULT 'INR',
    timezone VARCHAR(50) DEFAULT 'Asia/Kolkata',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### **Assets & Rates:**

```sql
CREATE TABLE assets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    is_active BOOLEAN DEFAULT TRUE,
    api_endpoint VARCHAR(255),
    icon_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE gold_rates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    gold_22k DECIMAL(10,2),
    gold_24k DECIMAL(10,2),
    silver DECIMAL(10,2),
    bitcoin DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_date (date)
);
```

## API Documentation

### **Authentication Endpoints:**

#### **POST /api/auth/signup**

```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "fullName": "John Doe",
  "mobileNo": "+1234567890",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main St"
}
```

#### **POST /api/auth/signin**

```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

#### **POST /api/auth/forgot-password**

```json
{
  "email": "user@example.com"
}
```

#### **POST /api/auth/reset-password**

```json
{
  "token": "secure-reset-token",
  "newPassword": "newSecurePassword123"
}
```

### **Rate Endpoints:**

#### **GET /api/rate/today**

```json
{
  "date": "2024-01-15",
  "gold22k": 5850.00,
  "gold24k": 6380.00,
  "silver": 75.50,
  "bitcoin": 3735000.00,
  "change22k": 25.00,
  "change24k": 30.00,
  "changePercent22k": 0.43,
  "changePercent24k": 0.47,
  "silverChange": 1.25,
  "silverChangePercent": 1.68,
  "bitcoinChange": 15000.00,
  "bitcoinChangePercent": 0.40,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### **GET /api/rate/history?days=10**

```json
{
  "rates": [
    {
      "date": "2024-01-15",
      "gold22k": 5850.00,
      "gold24k": 6380.00,
      "silver": 75.50,
      "bitcoin": 3735000.00
    }
  ]
}
```

### **Admin Endpoints:**

#### **GET /api/admin/assets**(Admin Only)

```json
[
  {
    "id": 1,
    "name": "gold",
    "displayName": "Gold",
    "symbol": "XAU",
    "isActive": true
  }
]
```

#### **POST /api/admin/rates/update**(Admin Only)

```json
{
  "assetName": "gold",
  "rate22k": 5875.00,
  "rate24k": 6405.00,
  "date": "2024-01-15"
}
```

## Deployment Architecture

### **Production Environment:**

#### **Frontend Deployment (Vercel/Netlify):**

```yaml
# netlify.toml
[build]
  publish = "out"
  command = "npm run build"

[build.environment]
  NODE_VERSION = "18"
  GOLD_API_BASE_URL = "https://api.knowallrates.com"

[[redirects]]
  from = "/api/*"
  to = "/.netlify/functions/:splat"
  status = 200
```

#### **Backend Deployment (Docker):**

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/goldapi-*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### **Environment Configuration:**

```shellscript
# Production Environment Variables
GOLD_API_BASE_URL=https://api.knowallrates.com
GOLD_API_KEY=your_production_api_key

# Database
DATABASE_URL=mysql://user:pass@host:3306/knowallrates
SPRING_DATASOURCE_URL=${DATABASE_URL}

# Email Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=noreply@knowallrates.com
SPRING_MAIL_PASSWORD=your_app_password

# JWT Security
APP_JWT_SECRET=your_super_secure_jwt_secret_key_here
APP_JWT_EXPIRATION=86400000

# Frontend URL
APP_FRONTEND_URL=https://knowallrates.com
```

## Performance & Optimization

### **Frontend Optimizations:**

- ⚡ **Next.js App Router**: Server-side rendering
- 🎯 **Code Splitting**: Automatic route-based splitting
- 🗜️ **Image Optimization**: Next.js Image component
- 💾 **Caching**: API response caching
- 📱 **Progressive Web App**: PWA capabilities


### **Backend Optimizations:**

- 🔄 **Async Processing**: Email sending and heavy operations
- ⏰ **Scheduled Tasks**: Token cleanup and data refresh
- 💾 **Database Indexing**: Optimized queries
- 🚀 **Connection Pooling**: Efficient database connections
- 📊 **Monitoring**: Health checks and metrics


### **Caching Strategy:**

```javascript
// Frontend Caching
const cacheConfig = {
  rates: '5 minutes',      // Live rate data
  history: '1 hour',       // Historical data
  settings: 'localStorage', // User preferences
  auth: 'sessionStorage'   // Authentication tokens
}
```

## Development Setup

### **Prerequisites:**

```shellscript
# Required Software
Node.js 18+
Java 17+
Maven 3.8+
Git
```

### **Frontend Setup:**

```shellscript
# Clone repository
git clone https://github.com/yourusername/knowallrates.git
cd knowallrates/frontend

# Install dependencies
npm install

# Environment setup
cp .env.example .env.local
# Edit .env.local with your configuration

# Start development server
npm run dev
```

### **Backend Setup:**

```shellscript
# Navigate to backend
cd knowallrates/backend

# Configure application.properties
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties

# Run application
mvn spring-boot:run
```

### **Database Setup:**

```sql
-- Create database
CREATE DATABASE knowallrates;

-- Create user
CREATE USER 'knowallrates'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON knowallrates.* TO 'knowallrates'@'localhost';
FLUSH PRIVILEGES;
```

## Testing Strategy

### **Frontend Testing:**

```shellscript
# Unit Tests
npm run test

# E2E Tests
npm run test:e2e

# Type Checking
npm run type-check

# Linting
npm run lint
```

### **Backend Testing:**

```shellscript
# Unit Tests
mvn test

# Integration Tests
mvn test -Dtest=**/*IntegrationTest

# Coverage Report
mvn jacoco:report
```

### **Test Coverage Goals:**

- 🎯 **Unit Tests**: >80% coverage
- 🔄 **Integration Tests**: Critical paths covered
- 🌐 **E2E Tests**: User journey validation
- 🔐 **Security Tests**: Authentication & authorization


## Monitoring & Analytics

### **Application Monitoring:**

```java
// Health Check Endpoint
@GetMapping("/api/health")
public ResponseEntity<Map<String, Object>> health() {
    Map<String, Object> status = new HashMap<>();
    status.put("status", "UP");
    status.put("timestamp", LocalDateTime.now());
    status.put("database", checkDatabaseHealth());
    status.put("externalApis", checkExternalApiHealth());
    return ResponseEntity.ok(status);
}
```

### **Key Metrics:**

- 📈 **User Engagement**: Daily/Monthly active users
- 🔄 **API Performance**: Response times and error rates
- 💰 **Rate Accuracy**:
