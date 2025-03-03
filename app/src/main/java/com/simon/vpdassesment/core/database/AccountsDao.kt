package com.simon.vpdassesment.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.simon.vpdassesment.features.home.model.CardModel
import kotlinx.coroutines.flow.Flow


@Dao
interface AccountsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<CardModel>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: CardModel): Long

    @Query("SELECT * FROM accounts WHERE accountNumber = :accountNumber")
    suspend fun getAccountById(accountNumber: Long): CardModel?

    @Delete
    suspend fun deleteAccount(account: CardModel)

    @Update
    suspend fun updateAccount(cardModel: CardModel)

    @Query("DELETE  FROM Accounts")
    @Transaction
    suspend fun deleteAll()

    @Query("SELECT * FROM accounts where userId = :userId")
    fun getAllUserAccounts(userId:String): Flow<List<CardModel>>




}