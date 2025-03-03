package com.simon.vpdassesment.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.simon.vpdassesment.core.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transactions: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updatePost(transaction: TransactionEntity)

    @Query("DELETE  FROM transactions")
    @Transaction
    suspend fun deleteAll()

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Transaction
    @Query("SELECT * FROM transactions  ORDER BY timeStampLong DESC LIMIT :pageSize OFFSET :page * :pageSize")
    fun fetchPaginatedPosts(
        page: Int = 0,
        pageSize: Int=10,
    ): Flow<List<TransactionEntity>>


}