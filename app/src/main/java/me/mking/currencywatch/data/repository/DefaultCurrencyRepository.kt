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

    override fun availableCurrencies(): Flow<List<CurrencyEntity>> {
        return dbCurrencyEntityDao.availableCurrencies()
            .cachedCurrenciesFlow { it.isNotEmpty() }
            .map { mapToCurrencyEntities(it) }
    }

    override fun getBaseCurrency(): Flow<CurrencyEntity> = dbCurrencyEntityDao.baseCurrencyEntity()
        .cachedCurrenciesFlow { it != null }
        .filterNotNull()
        .map(::mapToCurrencyEntity)

    override suspend fun setBaseCurrency(currencyEntity: CurrencyEntity) {
        dbCurrencyEntityDao.clearBaseCurrencies()
        dbCurrencyEntityDao.updateBaseCurrencyEntity(currencyEntity.code)
    }

    override fun getPreferredCurrencies(): Flow<List<CurrencyEntity>> {
        return dbCurrencyEntityDao.preferredCurrencyEntities()
            .cachedCurrenciesFlow { it.isNotEmpty() }
            .map { mapToCurrencyEntities(it) }
    }

    override suspend fun setPreferredCurrency(currencyEntity: CurrencyEntity) {
        dbCurrencyEntityDao.updatePreferredCurrencyEntity(currencyEntity.code)
    }

    private fun <T> Flow<T>.cachedCurrenciesFlow(useCache: (T) -> Boolean) = flatMapLatest {
        if (useCache.invoke(it)) {
            flowOf(it)
        } else {
            populateAvailableCurrencies()
            this
        }
    }

    private fun mapToCurrencyEntities(dbEntities: List<DbCurrencyEntity>) =
        dbEntities.map(::mapToCurrencyEntity)

    private fun mapToCurrencyEntity(dbEntity: DbCurrencyEntity) = CurrencyEntity(
        name = dbEntity.name,
        code = dbEntity.code,
        isBase = dbEntity.isBase,
        isPreferred = dbEntity.isPreferred
    )

    private suspend fun populateAvailableCurrencies() {
        val remoteResult = exchangeRateApi.getAvailableCurrencies()
        dbCurrencyEntityDao.insert(*remoteResult.symbols.map {
            DbCurrencyEntity(
                name = it.value["description"]!!,
                code = it.value["code"]!!,
                isBase = it.value["code"] == "GBP",
                isPreferred = it.value["code"] == "USD" || it.value["code"] == "EUR"
            )
        }.toTypedArray())
        if (dbCurrencyEntityDao.availableCurrencyCount() == 0) {
            throw NoCurrenciesException()
        }
    }
}

class NoCurrenciesException : Exception("No available currencies.")