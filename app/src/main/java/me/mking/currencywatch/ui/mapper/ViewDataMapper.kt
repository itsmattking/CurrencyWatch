package me.mking.currencywatch.ui.mapper

interface ViewDataMapper<S, T> {
    fun map(input: S): T
}