package me.mking.currencywatch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mking.currencywatch.domain.entity.CurrencyEntity

interface CurrencyRepository {
    fun available(): Flow<List<CurrencyEntity>>
    fun preferredBase(): Flow<CurrencyEntity>
    suspend fun setPreferredBase(currencyEntity: CurrencyEntity)
}