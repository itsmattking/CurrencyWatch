package me.mking.currencywatch.ui.mapper

import me.mking.currencywatch.ui.ViewData

interface ViewDataMapper<S : ViewDataMapperInput, T : ViewData> {
    fun map(input: S): T
}

interface ViewDataMapperInput