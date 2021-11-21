package me.mking.currencywatch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mking.currencywatch.domain.entity.CurrencyEntity

interface CurrencyRepository {
    fun availableCurrencies(): Flow<List<CurrencyEntity>>
    fun getBaseCurrency(): Flow<CurrencyEntity>
    suspend fun setBaseCurrency(currencyEntity: CurrencyEntity)
    fun getPreferredCurrencies(): Flow<List<CurrencyEntity>>
    suspend fun setPreferredCurrency(currencyEntity: CurrencyEntity)
}