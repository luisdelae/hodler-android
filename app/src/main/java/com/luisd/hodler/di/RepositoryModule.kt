package com.luisd.hodler.di

import com.luisd.hodler.data.remote.api.CoinGeckoApi
import com.luisd.hodler.data.repository.CoinRepositoryImpl
import com.luisd.hodler.domain.repository.CoinRepository
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
    ): CoinRepository {
        return CoinRepositoryImpl(api)
    }
}