package com.simon.vpdassesment.features.transfer

import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

data class TransferState(
    val sourceAccountNumber:Long?=null,
    val destinationAccountNumber:Long?=null,
    val destinationAccountName:String ?= null,
    val destinationBankId:String? = null,
    val amount:Long?=null,
    val description:String="",
    val transferError:String?=null,
    val transferrring:Boolean=false,

    val banks:List<Bank> = json.decodeFromString<BankResponse>(banksJson).data?: emptyList()
){
    override fun toString(): String {
        return "sourceaccount number: $sourceAccountNumber destinateAccountnumber: $destinationAccountNumber" +
                " destinationAccountname: $destinationAccountName destinationBankId: $destinationBankId" +
                "" +
                " amount: $amount description: $description"
    }
}
fun TransferState.isValid(): Boolean {
    return when {
        sourceAccountNumber == null || sourceAccountNumber.toString().length < 5 -> false
        destinationAccountNumber == null || destinationAccountNumber.toString().length < 5 -> false
        destinationAccountName.isNullOrEmpty() -> false
        destinationBankId.isNullOrEmpty() -> false
        amount == null || amount <= 0 -> false
        description.isBlank() -> false
        else -> true
    }
}
