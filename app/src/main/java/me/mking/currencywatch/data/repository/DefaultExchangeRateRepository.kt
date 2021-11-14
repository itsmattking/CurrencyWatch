package me.mking.currencywatch.data.repository

import me.mking.currencywatch.data.sources.ExchangeRateApi
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.entity.ExchangeRateEntity
import me.mking.currencywatch.domain.repository.ExchangeRateRepository
import javax.inject.Inject

class DefaultExchangeRateRepository @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi
) : ExchangeRateRepository {
    override suspend fun latest(base: CurrencyEntity): List<ExchangeRateEntity> {
        return exchangeRateApi.getLatestRates(base.code).rates.map {
            ExchangeRateEntity(it.key, it.value)
        }
    }
}