package me.mking.currencywatch.domain.repository

import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.entity.ExchangeRateEntity

interface ExchangeRateRepository {
    suspend fun latest(base: CurrencyEntity): List<ExchangeRateEntity>
}