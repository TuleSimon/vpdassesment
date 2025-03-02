package com.simon.vpdassesment.data.repositories

import com.simon.vpdassesment.core.entity.LoginEntity
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.AuthDatasource
import com.simon.vpdassesment.data.datasource.params.CreateAccountParams
import com.simon.vpdassesment.data.datasource.params.LoginParams
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authDatasource: AuthDatasource) : AuthRepository {

    override fun login(params: LoginParams): Flow<NetworkResource<LoginEntity>> {
        return authDatasource.login(params)
    }

    override fun createAccount(params: CreateAccountParams): Flow<NetworkResource<LoginEntity>> {
        return authDatasource.createAccount(params)
    }

}