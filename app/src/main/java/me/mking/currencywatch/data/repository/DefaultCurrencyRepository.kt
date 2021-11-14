package me.mking.currencywatch.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
    override fun available(): Flow<List<CurrencyEntity>> {
        return dbCurrencyEntityDao.availableCurrencies().flatMapLatest {
            if (it.isNotEmpty()) {
                flowOf(it)
            } else {
                populateAvailableCurrencies()
                dbCurrencyEntityDao.availableCurrencies()
            }
        }.map { mapToCurrencyEntities(it) }
    }

    override fun preferredBase(): Flow<CurrencyEntity> = dbCurrencyEntityDao.baseCurrencyEntity()
        .flatMapLatest {
            if (it == null) {
                populateAvailableCurrencies()
                dbCurrencyEntityDao.baseCurrencyEntity()
            } else {
                flowOf(it)
            }
        }
        .filterNotNull()
        .map(::mapToCurrencyEntity)

    override suspend fun setPreferredBase(currencyEntity: CurrencyEntity) {
        dbCurrencyEntityDao.clearBaseCurrencies()
        dbCurrencyEntityDao.updateBaseCurrencyEntity(currencyEntity.code)
    }

    private fun mapToCurrencyEntities(dbEntities: List<DbCurrencyEntity>) =
        dbEntities.map(::mapToCurrencyEntity)

    private fun mapToCurrencyEntity(dbEntity: DbCurrencyEntity) = CurrencyEntity(
        name = dbEntity.name,
        code = dbEntity.code,
        isBase = dbEntity.isBase
    )

    private suspend fun populateAvailableCurrencies() {
        val remoteResult = exchangeRateApi.getAvailableCurrencies()
        dbCurrencyEntityDao.insert(*remoteResult.symbols.map {
            DbCurrencyEntity(
                name = it.value["description"]!!,
                code = it.value["code"]!!,
                isBase = it.value["code"] == "GBP"
            )
        }.toTypedArray())
        if (dbCurrencyEntityDao.availableCurrencyCount() == 0) {
            throw NoCurrenciesException()
        }
    }
}

class NoCurrenciesException : Exception("No available currencies.")