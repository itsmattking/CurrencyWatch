package me.mking.currencywatch.domain.usecase

import me.mking.currencywatch.domain.repository.CurrencyRepository
import javax.inject.Inject

class SetBaseCurrencyEntityUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : NoOutputUseCase<SetBaseCurrencyEntityUseCaseInput> {
    override suspend fun execute(input: SetBaseCurrencyEntityUseCaseInput) {
        currencyRepository.setBaseCurrency(currencyRepository.getCurrencyByCode(input.code))
    }
}

@JvmInline
value class SetBaseCurrencyEntityUseCaseInput(
    val code: String
) : UseCaseInput