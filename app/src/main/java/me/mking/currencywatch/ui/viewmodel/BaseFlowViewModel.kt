package me.mking.currencywatch.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

abstract class BaseFlowViewModel<T : ViewData> : ViewModel() {

    private val mutableState: MutableStateFlow<ViewState<T>> =
        MutableStateFlow(ViewState.Idle())
    val state = mutableState

    protected suspend fun loadLatestFlowIntoState(flowBlock: () -> Flow<T>) {
        mutableState.emit(ViewState.Loading())
        collectLatestFlowIntoState(flowBlock)
    }

    private suspend fun collectLatestFlowIntoState(flowBlock: () -> Flow<T>) {
        try {
            flowBlock.invoke()
                .catch { mutableState.emit(ViewState.Error(it)) }
                .collectLatest {
                    mutableState.emit(ViewState.Ready(it))
                }
        } catch (exception: Exception) {
            mutableState.emit(ViewState.Error(exception))
        }
    }

    protected suspend fun updateSuccessState(block: suspend (T) -> T) {
        val currentState = state.value
        if (currentState is ViewState.Ready) {
            mutableState.emit(ViewState.Ready(block.invoke(currentState.data)))
        }
    }
}

sealed class ViewState<T> {
    class Idle<T> : ViewState<T>()
    class Loading<T> : ViewState<T>()
    data class Ready<T>(val data: T) : ViewState<T>()
    data class Error<T>(val throwable: Throwable) : ViewState<T>()
}

interface ViewData
