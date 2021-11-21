package me.mking.currencywatch.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.usecase.GetBaseCurrencyEntityFlowUseCase
import me.mking.currencywatch.domain.usecase.GetLatestExchangeRatesUseCase
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class LatestExchangeRatesViewModel @Inject constructor(
    private val getBaseCurrencyEntityFlowUseCase: GetBaseCurrencyEntityFlowUseCase,
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase
) : BaseFlowViewModel<LatestExchangeRatesViewData>() {

    private val _baseAmountFlow: MutableStateFlow<Double> = MutableStateFlow(0.00)
    private val baseAmountFlow: StateFlow<Double> = _baseAmountFlow

    fun load() = viewModelScope.launch {
        loadLatestFlowIntoState {
            combine(
                baseAmountFlow,
                getBaseCurrencyEntityFlowUseCase.execute()
                    .stateIn(this, SharingStarted.Lazily, CurrencyEntity.EMPTY)
                    .dropWhile { it == CurrencyEntity.EMPTY }
                    .flatMapConcat(getLatestExchangeRatesUseCase::execute)
            ) { base, result ->
                LatestExchangeRatesViewData(
                    base = result.base,
                    baseAmount = base,
                    baseCurrencySymbol = mapToCurrencySymbol(result.base.code),
                    rates = result.rates.map {
                        LatestExchangeRatesViewData.ExchangeRate(
                            name = it.name,
                            symbol = mapToCurrencySymbol(it.name),
                            value = BigDecimal(it.rate * base).setScale(3, BigDecimal.ROUND_DOWN).toDouble()
                        )
                    }
                )
            }

        }
    }

    fun setBaseAmount(baseAmount: Double) = viewModelScope.launch {
        _baseAmountFlow.emit(BigDecimal(baseAmount).setScale(2, BigDecimal.ROUND_DOWN).toDouble())
    }

    private fun mapToCurrencySymbol(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }
}

data class LatestExchangeRatesViewData(
    val base: CurrencyEntity,
    val baseAmount: Double,
    val baseCurrencySymbol: String,
    val rates: List<ExchangeRate>,
    val isReloading: Boolean = false
) : ViewData {
    data class ExchangeRate(
        val name: String,
        val symbol: String,
        val value: Double
    )
}