package me.mking.currencywatch.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_entities")
data class DbCurrencyEntity(
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "isBase") val isBase: Boolean
)