package com.simon.vpdassesment.core.preference

sealed class PrefKey( val key:String) {

    data class AccountBalance(val accountNumber:String):PrefKey(accountNumber)
}


fun PrefKey.getKey() : String{
    return key
}

