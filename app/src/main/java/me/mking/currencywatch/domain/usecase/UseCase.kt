package me.mking.currencywatch.domain.usecase

import kotlinx.coroutines.flow.Flow

interface UseCase<S : UseCaseInput, T : UseCaseResult> {
    suspend fun execute(input: S): T
}

interface NoInputUseCase<T : UseCaseResult> {
    suspend fun execute(): T
}

interface NoOutputUseCase<S : UseCaseInput> {
    suspend fun execute(input: S)
}

interface FlowUseCase<S : UseCaseInput, T : UseCaseResult> {
    fun execute(input: S): Flow<T>
}

interface FlowNoInputUseCase<T : UseCaseResult> {
    fun execute(): Flow<T>
}

interface UseCaseInput
interface UseCaseResult