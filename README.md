# Hodler - Cryptocurrency Trading Simulator

[![Build Status](https://github.com/luisdelae/hodler-android/workflows/Android%20CI/badge.svg)](https://github.com/luisdelae/hodler-android/actions)

> **hodler** (n.) - Someone who holds cryptocurrency long-term, regardless of market volatility.

Android cryptocurrency trading simulator with $10,000 virtual money. Building on the v1 portfolio tracker
foundation to add paper trading, transaction history, and gamification features.

**Target Completion:** March 2026

## Related Projects

- [Hodler Android V1](https://github.com/luisdelae/hodler-android/tree/main): Portfolio tracker foundation
- [Hodler Backend](https://github.com/luisdelae/hodler-backend): AWS Lambda APIs for leaderboards and authentication [WIP]
- [Hodler Landing](https://github.com/luisdelae/hodler-landing): React frontend (marketing site) [WIP]

## Quick Start
```bash
git clone https://github.com/luisdelae/hodler-android.git
git checkout develop-v2
./gradlew assembleDebug
```

## Branches
- `main` - Stable v1 (portfolio tracker)
- `develop-v2` - v2 in progress (trading simulator) ← **You are here**

## Planned Features

### Core Trading
- **Virtual Trading** - Practice trading with $10,000 virtual money
- **Transaction History** - Track all buy/sell trades with complete history
- **Portfolio Management** - Cash balance, holdings, and total value tracking
- **Profit/Loss Analytics** - Cost basis tracking and performance metrics

### Gamification & Competition
- **Achievement System** - Unlock badges for trading milestones
- **Local Leaderboard** - Track performance across multiple portfolio attempts
- **Advanced Orders** - Limit orders and stop losses (stretch goal)

### Technical Foundation
- **User Authentication** - AWS Cognito with JWT tokens
- **Cloud Sync** - Backend integration for cross-device access
- **Offline Support** - View portfolio and history offline, trade online
- **Clean Architecture** - Building on v1's proven architecture

## Tech Stack

| Category | Technology |
|----------|-----------|
| **Architecture** | Clean Architecture (3-layer) |
| **Language** | Kotlin 2.0 |
| **UI** | Jetpack Compose + Material 3 |
| **DI** | Hilt |
| **Async** | Coroutines + Flow |
| **Navigation** | Navigation Compose |
| **Network** | Retrofit + OkHttp + Moshi |
| **Database** | Room |
| **Charts** | Vico |
| **Testing** | JUnit, MockK, Turbine |
| **Backend** | AWS Lambda + DynamoDB |
| **Auth** | AWS Cognito (JWT) |

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 26+ (target SDK 34)

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/luisdelae/hodler-android.git
cd hodler-android
git checkout develop-v2
```

2. **Get a CoinGecko Demo API key**
   - Sign up at [CoinGecko API](https://www.coingecko.com/en/api/pricing)
   - Get a free Demo API key

3. **Add API key to `local.properties`**
```properties
COINGECKO_API_KEY=your_api_key_here
API_BASE_URL=https://api.yourdomain.com  # Optional
```

4. **Build and run**
```bash
./gradlew assembleDebug
```

## Development Roadmap

### Phase 1: Foundation (Weeks 1-2)
- [x] Backend authentication endpoints (AWS Lambda)
- [] Android auth integration with JWT
- [] User registration and login screens
- [] Token persistence and management

### Phase 2: Core Trading (Weeks 3-4)
- [ ] Wallet system with $10,000 starting balance
- [ ] Trade execution (buy/sell) with fee calculation
- [ ] Transaction recording
- [ ] Portfolio value calculation

### Phase 3: Portfolio & History (Weeks 5-6)
- [ ] Portfolio screen (cash + holdings + total value)
- [ ] Transaction history with filtering
- [ ] Profit/loss calculations
- [ ] Performance tracking

### Phase 4: Achievements (Week 7)
- [ ] Achievement system implementation
- [ ] Unlock notifications
- [ ] Progress tracking

### Phase 5: Leaderboard (Week 8)
- [ ] Local leaderboard
- [ ] Portfolio reset functionality
- [ ] Backend leaderboard integration

### Phase 6: Advanced Features (Weeks 9-10)
- [ ] Limit orders
- [ ] Stop loss orders
- [ ] Order monitoring
- [ ] Price alerts

### Phase 7: Polish & Testing (Weeks 11-12)
- [ ] UI tests for critical flows
- [ ] Performance optimization
- [ ] Bug fixes and edge cases
- [ ] Final polish

## Migration from v1

- **TDB**

## Testing

**Current Coverage:** 127 unit tests (v1 foundation)

**Focus Areas:**
- ViewModels and business logic
- Repository implementations
- Use cases and domain logic
- Data mapping layers

```bash
# Run tests
./gradlew test

# With coverage
./gradlew testDebugUnitTest jacocoTestReport
```

## License

This project is a portfolio demonstration and learning exercise. Not licensed for commercial use.

## Author

**Luis De La Espriella**  
[GitHub](https://github.com/luisdelae) • [LinkedIn](https://linkedin.com/in/luisdelaespriella)

---

**Status**: 🚧 In Active Development 🚧