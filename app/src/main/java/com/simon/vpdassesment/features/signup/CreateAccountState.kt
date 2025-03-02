package com.simon.vpdassesment.features.signup

data class CreateAccountState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val verifypassword: String = "",
    val error: String? = null,
    val servererror: String? = null,
    val errorPassword: String? = null,
    val errorverifyPassword: String? = null,
    val errorname: String? = null,
    val loading: Boolean = false
) {
    fun isValid(): Boolean {
        return error.isNullOrEmpty() &&
                errorPassword.isNullOrEmpty() &&
                errorverifyPassword.isNullOrEmpty() &&
                errorname.isNullOrEmpty() &&
                email.isNotBlank() &&
                name.isNotBlank() &&
                password.isNotBlank() &&
                verifypassword.isNotBlank() &&
                password == verifypassword
    }
}