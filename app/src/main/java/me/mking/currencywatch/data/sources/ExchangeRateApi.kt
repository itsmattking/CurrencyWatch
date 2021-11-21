package me.mking.currencywatch.data.sources

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {
    @GET("latest")
    suspend fun getLatestRates(@Query("base") baseCurrency: String, @Query("symbols") symbols: String): ExchangeRateApiResponse.LatestRates
    @GET("symbols")
    suspend fun getAvailableCurrencies(): ExchangeRateApiResponse.Currencies
}

@Serializable
object ExchangeRateApiResponse {
    @Serializable
    data class LatestRates(val success: Boolean, val rates: Map<String, Double>)
    @Serializable
    data class Currencies(val success: Boolean, val symbols: Map<String, Map<String, String>>)
}