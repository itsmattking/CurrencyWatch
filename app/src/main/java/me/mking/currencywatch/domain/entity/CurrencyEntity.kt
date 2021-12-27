package me.mking.currencywatch.domain.entity

data class CurrencyEntity(
    val name: String,
    val code: String
) {
    companion object {
        val EMPTY = CurrencyEntity("EMPTY", "EMPTY")
    }
}