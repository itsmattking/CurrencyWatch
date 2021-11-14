package me.mking.currencywatch.domain.usecase

import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.repository.CurrencyRepository
import javax.inject.Inject

class SetBaseCurrencyEntityUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun execute(currencyEntity: CurrencyEntity) {
        currencyRepository.setPreferredBase(currencyEntity)
    }
}