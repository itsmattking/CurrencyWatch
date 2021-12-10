package me.mking.currencywatch.domain.usecase

import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetBaseCurrencyEntityFlowUseCase @Inject constructor(
    private val repository: CurrencyRepository
) : FlowNoInputUseCase<GetBaseCurrencyEntityFlowUseCaseResult> {

    override fun execute() = repository.getBaseCurrency().filterNotNull().map {
        GetBaseCurrencyEntityFlowUseCaseResult(it)
    }
}

data class GetBaseCurrencyEntityFlowUseCaseResult(
    val baseCurrencyEntity: CurrencyEntity
) : UseCaseResult {
    companion object {
        val EMPTY = GetBaseCurrencyEntityFlowUseCaseResult(CurrencyEntity.EMPTY)
    }
}