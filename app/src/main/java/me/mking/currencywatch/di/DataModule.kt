package me.mking.currencywatch.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.mking.currencywatch.data.db.CurrencyWatchDatabase
import me.mking.currencywatch.data.repository.DefaultCurrencyRepository
import me.mking.currencywatch.data.repository.DefaultExchangeRateRepository
import me.mking.currencywatch.data.sources.ExchangeRateApi
import me.mking.currencywatch.domain.repository.CurrencyRepository
import me.mking.currencywatch.domain.repository.ExchangeRateRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    @Singleton
    fun exchangeRateApi(retrofit: Retrofit): ExchangeRateApi {
        return retrofit.create(ExchangeRateApi::class.java)
    }

    @Provides
    fun exchangeRateRepository(defaultExchangeRateRepository: DefaultExchangeRateRepository): ExchangeRateRepository =
        defaultExchangeRateRepository

    @Provides
    fun currencyRepository(defaultCurrencyRepository: DefaultCurrencyRepository): CurrencyRepository =
        defaultCurrencyRepository

    @Provides
    @Singleton
    fun exchangeRateDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        CurrencyWatchDatabase::class.java, "exchange-rate-db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun dbCurrencyEntityDao(db: CurrencyWatchDatabase) = db.currencyEntityDao()
}