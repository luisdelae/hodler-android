package com.luisd.hodler.data.remote.api

import com.luisd.hodler.data.remote.dto.CoinDetailDto
import com.luisd.hodler.data.remote.dto.CoinDto
import com.luisd.hodler.data.remote.dto.MarketChartDto
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("coins/{coinId}")
    suspend fun getCoinDetails(
        @Path("id") coinId: String,
        @Query("tickers") tickers: Boolean = false,
        @Query("community_date") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
    ): CoinDetailDto

    @GET("coins/{coinId}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") coinId: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: String = "1",
    ): MarketChartDto

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }
}