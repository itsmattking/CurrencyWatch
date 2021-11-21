package me.mking.currencywatch.domain.entity

data class CurrencyEntity(
    val name: String,
    val code: String,
    val isBase: Boolean = false,
    val isPreferred: Boolean = false
) {
    companion object {
        val EMPTY = CurrencyEntity("EMPTY", "EMPTY", false)
    }
}