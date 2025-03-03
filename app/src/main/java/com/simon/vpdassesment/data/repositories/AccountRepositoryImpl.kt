package com.simon.vpdassesment.data.repositories

import com.simon.vpdassesment.core.entity.TransactionEntity
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.AccountDataSource
import com.simon.vpdassesment.data.datasource.AuthDatasource
import com.simon.vpdassesment.data.datasource.params.GetAccountsParams
import com.simon.vpdassesment.data.datasource.params.TransferParams
import com.simon.vpdassesment.features.home.model.CardModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(private val accountDataSource: AccountDataSource) :
    AccountRepository {
    override fun getAccounts(params: GetAccountsParams): Flow<List<CardModel>> {
        return accountDataSource.getAccounts(params)
    }
    override fun makeTransfer(params: TransferParams): Flow<NetworkResource<TransactionEntity>> = accountDataSource.makeTransfer(params)
}