package me.mking.currencywatch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.mking.currencywatch.data.db.dao.DbCurrencyEntityDao
import me.mking.currencywatch.data.db.entity.DbCurrencyEntity

@Database(entities = [DbCurrencyEntity::class], version = 2)
abstract class CurrencyWatchDatabase : RoomDatabase() {
    abstract fun currencyEntityDao(): DbCurrencyEntityDao
}