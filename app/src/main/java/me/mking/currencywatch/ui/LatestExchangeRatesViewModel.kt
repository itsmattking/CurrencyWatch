package me.mking.currencywatch.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.usecase.*
import me.mking.currencywatch.ui.mapper.LatestExchangeRatesViewDataInput
import me.mking.currencywatch.ui.mapper.LatestExchangeRatesViewDataMapper
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@HiltViewModel
class LatestExchangeRatesViewModel @Inject constructor(
    private val getBaseCurrencyEntityFlowUseCase: GetBaseCurrencyEntityFlowUseCase,
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase,
    private val setBaseCurrencyEntityUseCase: SetBaseCurrencyEntityUseCase,
    private val latestExchangeRatesViewDataMapper: LatestExchangeRatesViewDataMapper
) : BaseFlowViewModel<LatestExchangeRatesViewData>() {

    private val _baseAmountFlow: MutableStateFlow<String> = MutableStateFlow("0.00")
    private val baseAmountFlow: StateFlow<String> = _baseAmountFlow

    fun load() = viewModelScope.launch {
        loadLatestFlowIntoState {
            combine(
                baseAmountFlow,
                getBaseCurrencyEntityFlowUseCase.execute()
                    .stateIn(this, SharingStarted.Lazily, GetBaseCurrencyEntityFlowUseCaseResult.EMPTY)
                    .dropWhile { it == GetBaseCurrencyEntityFlowUseCaseResult.EMPTY }
                    .map { GetLatestExchangeRatesInput(it.baseCurrencyEntity) }
                    .flatMapLatest(getLatestExchangeRatesUseCase::execute)
            ) { baseAmount, latestExchangeRatesResult ->
                latestExchangeRatesViewDataMapper.map(
                    LatestExchangeRatesViewDataInput(
                        baseAmount,
                        latestExchangeRatesResult
                    )
                )
            }
        }
    }

    fun setBaseAmount(baseAmount: String) = viewModelScope.launch {
        _baseAmountFlow.emit(baseAmount)
    }

    fun setBaseCurrency(code: String) = viewModelScope.launch {
        setBaseCurrencyEntityUseCase.execute(SetBaseCurrencyEntityUseCaseInput(code))
    }
}

data class LatestExchangeRatesViewData(
    val baseCurrency: CurrencyEntity,
    val baseAmount: String,
    val baseCurrencySymbol: String,
    val rates: List<ExchangeRate>,
    val isReloading: Boolean = false
) : ViewData {
    data class ExchangeRate(
        val name: String,
        val rate: String,
        val symbol: String,
        val value: String,
        val clickEvent: ExchangeRateClickEvent
    )
}

data class ExchangeRateClickEvent(val currencyName: String)