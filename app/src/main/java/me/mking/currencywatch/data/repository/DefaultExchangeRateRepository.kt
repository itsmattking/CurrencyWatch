package me.mking.currencywatch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.mking.currencywatch.data.sources.ExchangeRateApi
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.entity.ExchangeRateEntity
import me.mking.currencywatch.domain.repository.CurrencyRepository
import me.mking.currencywatch.domain.repository.ExchangeRateRepository
import javax.inject.Inject

class DefaultExchangeRateRepository @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi
) : ExchangeRateRepository {
    override fun latest(
        baseCurrencyEntity: CurrencyEntity,
        preferredCurrencies: List<CurrencyEntity>
    ): Flow<List<ExchangeRateEntity>> {
        return flow {
            emit(
                exchangeRateApi.getLatestRates(
                    baseCurrencyEntity.code,
                    preferredCurrencies.joinToString(",") { it.code }
                ).rates.map { ExchangeRateEntity(it.key, it.value) }
            )
        }
    }
}