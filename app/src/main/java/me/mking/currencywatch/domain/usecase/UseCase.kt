package me.mking.currencywatch.domain.usecase

import kotlinx.coroutines.flow.Flow

interface UseCase<S : UseCaseInput, T : UseCaseResult> {
    fun execute(input: S): T
}

interface FlowUseCase<S : UseCaseInput, T : UseCaseResult> {
    fun execute(input: S): Flow<T>
}

interface NoInputUseCase<T : UseCaseResult> {
    fun execute(): T
}

interface FlowNoInputUseCase<T : UseCaseResult> {
    fun execute(): Flow<T>
}

interface UseCaseInput
interface UseCaseResult