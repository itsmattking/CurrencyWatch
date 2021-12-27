package me.mking.currencywatch.data.repository

import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import me.mking.currencywatch.data.backFilledWith
import me.mking.currencywatch.data.db.dao.DbCurrencyEntityDao
import me.mking.currencywatch.data.db.entity.DbCurrencyEntity
import me.mking.currencywatch.data.sources.ExchangeRateApi
import me.mking.currencywatch.domain.entity.CurrencyEntity
import me.mking.currencywatch.domain.repository.CurrencyRepository
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DefaultCurrencyRepository @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi,
    private val dbCurrencyEntityDao: DbCurrencyEntityDao
) : CurrencyRepository {

    override fun availableCurrencies(): Flow<List<CurrencyEntity>> {
        return dbCurrencyEntityDao.availableCurrencies()
            .backFilledWith(::populateAvailableCurrencies)
            .map { mapToCurrencyEntities(it) }
    }

    override fun getBaseCurrency(): Flow<CurrencyEntity> = dbCurrencyEntityDao.baseCurrencyEntity()
        .backFilledWith(::populateAvailableCurrencies)
        .filterNotNull()
        .map(::mapToCurrencyEntity)

    override suspend fun setBaseCurrency(currencyEntity: CurrencyEntity) {
        dbCurrencyEntityDao.swapBaseAsPreferred()
        dbCurrencyEntityDao.updateCurrencyEntity(
            dbCurrencyEntityDao.getCurrencyEntityByCode(
                currencyEntity.code
            ).copy(isBase = true, isPreferred = false)
        )
    }

    override fun getPreferredCurrencies(): Flow<List<CurrencyEntity>> {
        return dbCurrencyEntityDao.preferredCurrencyEntities()
            .backFilledWith(::populateAvailableCurrencies)
            .map { mapToCurrencyEntities(it) }
    }

    override suspend fun setPreferredCurrency(currencyEntity: CurrencyEntity) {
        dbCurrencyEntityDao.updateCurrencyEntity(
            dbCurrencyEntityDao.getCurrencyEntityByCode(
                currencyEntity.code
            ).copy(isBase = false, isPreferred = true)
        )
    }

    override suspend fun getCurrencyByCode(code: String): CurrencyEntity {
        return mapToCurrencyEntity(dbCurrencyEntityDao.getCurrencyEntityByCode(code))
    }

    private fun mapToCurrencyEntities(dbEntities: List<DbCurrencyEntity>) =
        dbEntities.map(::mapToCurrencyEntity)

    private fun mapToCurrencyEntity(dbEntity: DbCurrencyEntity) = CurrencyEntity(
        name = dbEntity.name,
        code = dbEntity.code
    )

    private suspend fun populateAvailableCurrencies() {
        val remoteResult = exchangeRateApi.getAvailableCurrencies()
        dbCurrencyEntityDao.insert(*remoteResult.symbols.mapNotNull {
            try {
                DbCurrencyEntity(
                    name = it.value.getValue("description"),
                    code = it.value.getValue("code"),
                    isBase = isDefaultBase(it.value.getValue("code")),
                    isPreferred = isDefaultPreferred(it.value.getValue("code"))
                )
            } catch (exception: NoSuchElementException) {
                Log.e("Parse fail", "Parsing available currency failed: $exception")
                null
            }
        }.toTypedArray())
        if (dbCurrencyEntityDao.availableCurrencyCount() == 0) {
            throw NoCurrenciesException()
        }
    }

    private fun isDefaultPreferred(code: String) = code in setOf("USD", "EUR")
    private fun isDefaultBase(code: String) = code == "GBP"
}

class NoCurrenciesException : Exception("No available currencies.")