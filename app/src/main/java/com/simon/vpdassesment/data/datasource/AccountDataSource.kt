package com.simon.vpdassesment.data.datasource

import com.simon.vpdassesment.core.entity.LoginEntity
import com.simon.vpdassesment.core.entity.TransactionEntity
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.params.CreateAccountParams
import com.simon.vpdassesment.data.datasource.params.GetAccountsParams
import com.simon.vpdassesment.data.datasource.params.LoginParams
import com.simon.vpdassesment.data.datasource.params.TransferParams
import com.simon.vpdassesment.features.home.model.CardModel
import kotlinx.coroutines.flow.Flow

interface AccountDataSource {


    fun getAccounts(params: GetAccountsParams): Flow<List<CardModel>>
    fun makeTransfer(params: TransferParams): Flow<NetworkResource<TransactionEntity>>



}