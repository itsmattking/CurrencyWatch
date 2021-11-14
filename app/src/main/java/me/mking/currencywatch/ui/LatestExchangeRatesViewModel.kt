package me.mking.currencywatch.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.usecase.GetBaseCurrencyEntityFlowUseCase
import me.mking.currencywatch.domain.usecase.GetLatestExchangeRatesUseCase
import me.mking.currencywatch.domain.usecase.SetBaseCurrencyEntityUseCase
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class LatestExchangeRatesViewModel @Inject constructor(
    private val getBaseCurrencyEntityFlowUseCase: GetBaseCurrencyEntityFlowUseCase,
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase,
    private val setBaseCurrencyEntityUseCase: SetBaseCurrencyEntityUseCase
) : BaseFlowViewModel<LatestExchangeRatesViewData>() {

    fun load() = viewModelScope.launch {
        loadLatestFlowIntoState {
            getBaseCurrencyEntityFlowUseCase.execute()
                .stateIn(this, SharingStarted.Lazily, CurrencyEntity.EMPTY)
                .dropWhile { it == CurrencyEntity.EMPTY }
                .flatMapConcat(getLatestExchangeRatesUseCase::execute)
                .map { result ->
                    LatestExchangeRatesViewData(
                        base = result.base,
                        rates = result.rates.map {
                            LatestExchangeRatesViewData.ExchangeRate(
                                name = it.name,
                                value = it.rate
                            )
                        }
                    )
                }
        }
    }

    fun setOtherBase(currencyEntity: CurrencyEntity) = viewModelScope.launch {
        updateSuccessState {
            it.copy(isReloading = true)
        }
        setBaseCurrencyEntityUseCase.execute(currencyEntity)
    }
}

data class LatestExchangeRatesViewData(
    val base: CurrencyEntity,
    val rates: List<ExchangeRate>,
    val isReloading: Boolean = false
) : ViewData {
    data class ExchangeRate(
        val name: String,
        val value: Double
    )
}