package com.luisd.hodler.di

import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import com.luisd.hodler.domain.usecase.GetHoldingsWithPricesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetHoldingsWithPricesUseCase(
        portfolioRepository: PortfolioRepository,
        coinRepository: CoinRepository,
    ): GetHoldingsWithPricesUseCase {
        return GetHoldingsWithPricesUseCase(
            portfolioRepository = portfolioRepository,
            coinRepository =  coinRepository
        )
    }
}