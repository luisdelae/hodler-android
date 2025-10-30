package com.luisd.hodler.di

import com.luisd.hodler.data.local.dao.CachedCoinDao
import com.luisd.hodler.data.local.dao.CachedCoinDetailDao
import com.luisd.hodler.data.local.dao.HoldingDao
import com.luisd.hodler.data.remote.api.CoinGeckoApi
import com.luisd.hodler.data.repository.CoinRepositoryImpl
import com.luisd.hodler.data.repository.PortfolioRepositoryImpl
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideCoinRepository(
        api: CoinGeckoApi,
        cachedCoinDao: CachedCoinDao,
        cachedCoinDetailDao: CachedCoinDetailDao,
    ): CoinRepository {
        return CoinRepositoryImpl(
            api,
            cachedCoinDao,
            cachedCoinDetailDao,
        )
    }

    @Provides
    @Singleton
    fun providePortfolioRepository(
        holdingDao: HoldingDao,
    ): PortfolioRepository {
        return PortfolioRepositoryImpl(holdingDao)
    }
}
