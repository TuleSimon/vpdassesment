package com.simon.vpdassesment.data.datasource

import com.simon.vpdassesment.core.entity.LoginEntity
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.params.CreateAccountParams
import com.simon.vpdassesment.data.datasource.params.LoginParams
import kotlinx.coroutines.flow.Flow

interface AuthDatasource {


    fun login(params: LoginParams): Flow<NetworkResource<LoginEntity>>

    fun createAccount(params: CreateAccountParams): Flow<NetworkResource<LoginEntity>>

}