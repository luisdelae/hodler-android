package com.luisd.hodler.di

import android.content.Context
import androidx.room.Room
import com.luisd.hodler.data.local.dao.HoldingDao
import com.luisd.hodler.data.local.database.HodlerDatabase
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
        ).build()
    }

    @Provides
    fun provideHoldingDao(database: HodlerDatabase): HoldingDao {
        return database.holdingDao()
    }
}