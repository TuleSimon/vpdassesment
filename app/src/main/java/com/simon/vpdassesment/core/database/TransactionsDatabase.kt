package com.simon.vpdassesment.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simon.vpdassesment.core.entity.TransactionEntity
import com.simon.vpdassesment.features.home.model.CardModel


@Database(entities = [TransactionEntity::class,CardModel::class],
    version = 1, exportSchema = false)
abstract class TransactionsDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionsDao
    abstract fun accountDao(): AccountsDao

}