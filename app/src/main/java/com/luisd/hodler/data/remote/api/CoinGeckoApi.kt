package com.luisd.hodler.data.remote.api

import com.luisd.hodler.data.remote.dto.CoinDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {
    @GET("coins/markets")
    suspend fun getMarketCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
    ): List<CoinDto>

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }
}