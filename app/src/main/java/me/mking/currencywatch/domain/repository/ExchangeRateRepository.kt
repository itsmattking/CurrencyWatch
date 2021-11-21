package me.mking.currencywatch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.entity.ExchangeRateEntity

interface ExchangeRateRepository {
    fun latest(base: CurrencyEntity): Flow<List<ExchangeRateEntity>>
}