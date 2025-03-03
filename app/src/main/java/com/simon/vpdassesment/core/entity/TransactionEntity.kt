package com.simon.vpdassesment.core.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val transactionId: String = UUID.randomUUID().toString(),
    val amount:Long ,
    val description:String,
    val sourceAccountNumber:Long,
    val destinationAccount:Long,
    val destinationBankId:String,
    val destinationUserName:String,
    val timeStampLong:Long
)


