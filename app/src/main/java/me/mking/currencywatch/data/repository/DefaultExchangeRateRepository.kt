package me.mking.currencywatch.data.repository

import kotlinx.coroutines.flow.*
import me.mking.currencywatch.data.sources.ExchangeRateApi
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.entity.ExchangeRateEntity
import me.mking.currencywatch.domain.repository.CurrencyRepository
import me.mking.currencywatch.domain.repository.ExchangeRateRepository
import javax.inject.Inject

class DefaultExchangeRateRepository @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi,
    private val currencyRepository: CurrencyRepository
) : ExchangeRateRepository {
    override fun latest(base: CurrencyEntity): Flow<List<ExchangeRateEntity>> {
       return currencyRepository.getPreferredCurrencies().map { currencyList ->
           exchangeRateApi.getLatestRates(base.code, currencyList.joinToString(",") { it.code }).rates.map {
               ExchangeRateEntity(it.key, it.value)
           }
       }
    }
}