package me.mking.currencywatch.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
class BackfilledFlow<T>(val flow: Flow<T>) {
    var isNotEmpty: ((T) -> Boolean)? = null
    var backfillBlock: (suspend () -> Unit)? = null

    fun toFlow(): Flow<T> = flow.flatMapLatest {
        if (isNotEmpty?.invoke(it) == true) {
            flowOf(it)
        } else {
            backfillBlock?.invoke()
            flow
        }
    }
}

@ExperimentalCoroutinesApi
inline fun <reified T> Flow<T>.backfilledWith(noinline backfillBlock: suspend () -> Unit) =
    BackfilledFlow(this).apply {
        isNotEmpty = {
            when (it) {
                is List<*> -> it.isNotEmpty()
                else -> it != null
            }
        }
        this.backfillBlock = backfillBlock
    }.toFlow()
