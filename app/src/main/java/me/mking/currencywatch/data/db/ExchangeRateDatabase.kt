package me.mking.currencywatch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.mking.currencywatch.data.db.dao.DbCurrencyEntityDao
import me.mking.currencywatch.data.db.entity.DbCurrencyEntity

@Database(entities = [DbCurrencyEntity::class], version = 1)
abstract class ExchangeRateDatabase : RoomDatabase() {
    abstract fun currencyEntityDao(): DbCurrencyEntityDao
}