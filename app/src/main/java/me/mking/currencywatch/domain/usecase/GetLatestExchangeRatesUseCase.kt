package me.mking.currencywatch.domain.usecase

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.entity.ExchangeRateEntity
import me.mking.currencywatch.domain.repository.CurrencyRepository
import me.mking.currencywatch.domain.repository.ExchangeRateRepository
import javax.inject.Inject

@FlowPreview
class GetLatestExchangeRatesUseCase @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val currencyRepository: CurrencyRepository
) : FlowUseCase<GetLatestExchangeRatesInput, GetLatestExchangeRatesResult> {

    override fun execute(input: GetLatestExchangeRatesInput): Flow<GetLatestExchangeRatesResult> =
        currencyRepository.getPreferredCurrencies().flatMapConcat {
            exchangeRateRepository.latest(input.baseCurrencyEntity, it).map { rates ->
                GetLatestExchangeRatesResult(
                    baseCurrencyEntity = input.baseCurrencyEntity,
                    rates = rates
                )
            }
        }
}

data class GetLatestExchangeRatesResult(
    val baseCurrencyEntity: CurrencyEntity,
    val rates: List<ExchangeRateEntity>
) : UseCaseResult

data class GetLatestExchangeRatesInput(
    val baseCurrencyEntity: CurrencyEntity
) : UseCaseInput