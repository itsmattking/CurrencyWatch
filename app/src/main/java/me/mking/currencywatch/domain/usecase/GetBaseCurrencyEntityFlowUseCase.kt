package me.mking.currencywatch.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetBaseCurrencyEntityFlowUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {

    fun execute(): Flow<CurrencyEntity> = repository.getBaseCurrency().filterNotNull()
}