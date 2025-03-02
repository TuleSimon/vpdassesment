package com.simon.vpdassesment.features.login

data class LoginState(
    val email: String="",
    val password:String="",
    val error:String?=null,
    val servererror:String?=null,
    val errorPassword:String?=null,
    val loading:Boolean=false
)