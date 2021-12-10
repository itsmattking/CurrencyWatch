package me.mking.currencywatch.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
class BackFilledFlow<T>(val flow: Flow<T>) {
    var isNotEmpty: ((T) -> Boolean)? = null
    var backFillBlock: (suspend () -> Unit)? = null

    fun toFlow(): Flow<T> = flow.flatMapLatest {
        if (isNotEmpty?.invoke(it) == true) {
            flowOf(it)
        } else {
            backFillBlock?.invoke()
            flow
        }
    }
}

@ExperimentalCoroutinesApi
inline fun <reified T> Flow<T>.backFilledWith(noinline backFillBlock: suspend () -> Unit) =
    BackFilledFlow(this).apply {
        isNotEmpty = {
            when (it) {
                is List<*> -> it.isNotEmpty()
                else -> it != null
            }
        }
        this.backFillBlock = backFillBlock
    }.toFlow()
