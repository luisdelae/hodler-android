package com.luisd.hodler.di

import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import com.luisd.hodler.domain.usecase.ObservePortfolioUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideObservePortfolioUseCase(
        portfolioRepository: PortfolioRepository,
        coinRepository: CoinRepository,
    ): ObservePortfolioUseCase {
        return ObservePortfolioUseCase(
            portfolioRepository = portfolioRepository,
            coinRepository =  coinRepository
        )
    }
}