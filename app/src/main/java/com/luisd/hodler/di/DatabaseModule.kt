package com.luisd.hodler.di

import android.content.Context
import androidx.room.Room
import com.luisd.hodler.data.local.dao.CachedCoinDao
import com.luisd.hodler.data.local.dao.CachedCoinDetailDao
import com.luisd.hodler.data.local.dao.HoldingDao
import com.luisd.hodler.data.local.database.HodlerDatabase
import com.luisd.hodler.data.local.database.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideHolderDatabase(
        @ApplicationContext context: Context
    ): HodlerDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            HodlerDatabase::class.java,
            "hodler_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideHoldingDao(database: HodlerDatabase): HoldingDao {
        return database.holdingDao()
    }

    @Provides
    fun provideCachedCoinDao(database: HodlerDatabase): CachedCoinDao {
        return database.cachedCoinDao()
    }

    @Provides
    fun provideCachedCoinDetailDao(database: HodlerDatabase): CachedCoinDetailDao {
        return database.cachedCoinDetailDao()
    }
}
