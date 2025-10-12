# Hodler

Android cryptocurrency portfolio tracker built with Clean Architecture, Jetpack Compose, and modern Android development practices.

> **hodler** (n.) - Someone who holds cryptocurrency long-term, regardless of market volatility.

## Project Goals

- Demonstrate **Clean Architecture** with clear separation of concerns
- Implement **offline-first** strategy with Room + Retrofit
- Practice **reactive programming** with Kotlin Flow and Coroutines
- Build **production-ready** patterns for mid-senior level demonstration

## Architecture

- **Pattern**: MVVM with MVI-inspired state management
- **DI**: Hilt
- **UI**: Jetpack Compose + Material 3
- **Networking**: Retrofit + OkHttp + Moshi
- **Database**: Room with Flow
- **Charts**: Vico
- **Testing**: JUnit, MockK, Turbine (target: 60-70% coverage)

## Features

### Implemented
- [ ] Market overview (top cryptocurrencies)
- [ ] Coin detail with price charts
- [ ] Portfolio tracking with holdings
- [ ] Offline-first caching
- [ ] Search functionality
- [ ] Add/edit/delete holdings

### Future Enhancements
- Price alerts
- Widgets
- Biometric security
- Transaction history

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Architecture** | Clean Architecture (Domain/Data/Presentation) |
| **DI** | Hilt |
| **Async** | Kotlin Coroutines + Flow |
| **UI** | Jetpack Compose + Material 3 |
| **Navigation** | Navigation Compose (type-safe) |
| **Networking** | Retrofit + OkHttp + Moshi |
| **Database** | Room |
| **Images** | Coil |
| **Charts** | Vico |
| **Testing** | JUnit 4, MockK, Turbine, Coroutines Test |

## API

Using [CoinGecko API](https://www.coingecko.com/api/documentation) (free tier, no API key required)

## Running the Project

1. Clone the repository
2. Open in Android Studio Hedgehog or later
3. Sync Gradle
4. Run on device/emulator (Min SDK 26)

## Development Timeline

**Week 1**: Foundation + Market/Detail screens  
**Week 2**: Portfolio + Holdings management  
**Week 3**: Testing + Documentation + Polish

Target completion: **85%** functional by November 1-ish, 2025

---

**Status**: 🚧 In Active Development 🚧
