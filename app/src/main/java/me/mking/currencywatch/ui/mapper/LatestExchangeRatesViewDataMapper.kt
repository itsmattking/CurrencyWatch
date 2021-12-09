package me.mking.currencywatch.ui.mapper

import me.mking.currencywatch.domain.usecase.GetLatestExchangeRatesResult
import me.mking.currencywatch.ui.LatestExchangeRatesViewData
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class LatestExchangeRatesViewDataMapper @Inject constructor() :
    ViewDataMapper<Pair<String, GetLatestExchangeRatesResult>, LatestExchangeRatesViewData> {
    override fun map(input: Pair<String, GetLatestExchangeRatesResult>): LatestExchangeRatesViewData {
        val (base, result) = input
        val baseDouble = BigDecimal(base).setScale(3, BigDecimal.ROUND_HALF_UP).toDouble()
        return LatestExchangeRatesViewData(
            baseCurrency = result.base,
            baseAmount = base,
            baseCurrencySymbol = mapToCurrencySymbol(result.base.code),
            rates = result.rates.map {
                LatestExchangeRatesViewData.ExchangeRate(
                    name = it.name,
                    rate = String.format("%,.3f", it.rate),
                    symbol = mapToCurrencySymbol(it.name),
                    value = String.format(
                        "%,.3f",
                        BigDecimal(it.rate * baseDouble).setScale(3, BigDecimal.ROUND_HALF_UP)
                            .toDouble()
                    )
                )
            }
        )
    }

    private fun mapToCurrencySymbol(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }
}