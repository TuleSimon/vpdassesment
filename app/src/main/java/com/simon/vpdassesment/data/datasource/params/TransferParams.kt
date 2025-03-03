package com.simon.vpdassesment.data.datasource.params

import com.simon.vpdassesment.core.entity.TransactionEntity
import java.util.Calendar

data class TransferParams(
    val destinationAccountNumber:Long,
    val sourceAccountNumber:Long,
    val destinationAccountName:String,
    val description:String,
    val amount:Long,
    val destinationBankId:String
) {
    fun toTransactionEntity(): TransactionEntity {
        return TransactionEntity(
            amount = amount,
            description =description,
            destinationAccount = destinationAccountNumber,
            destinationBankId = destinationBankId,
            destinationUserName = destinationAccountName,
            sourceAccountNumber = sourceAccountNumber,
            timeStampLong = Calendar.getInstance().timeInMillis
        )
    }
}