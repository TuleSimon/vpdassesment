package com.simon.vpdassesment.data.datasource

import android.net.TrafficStats
import com.simon.vpdassesment.core.database.AccountsDao
import com.simon.vpdassesment.core.database.TransactionsDao
import com.simon.vpdassesment.core.entity.TransactionEntity
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.core.preference.PrefKey
import com.simon.vpdassesment.core.preference.PreferenceDatastoreManager
import com.simon.vpdassesment.data.datasource.params.GetAccountsParams
import com.simon.vpdassesment.data.datasource.params.TransferParams
import com.simon.vpdassesment.features.home.model.CardModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlin.random.Random

class AccountDataSourceImpl(
    private val dao: TransactionsDao,
    private val accountDao: AccountsDao,
) : AccountDataSource {
    override fun getAccounts(params: GetAccountsParams): Flow<List<CardModel>> =
        accountDao.getAllUserAccounts(params.userId)


    override fun makeTransfer(params: TransferParams): Flow<NetworkResource<TransactionEntity>> =
        flow {
            val key = PrefKey.AccountBalance(params.sourceAccountNumber.toString())
            val balance = accountDao.getAccountById(params.sourceAccountNumber)?.balance
            if (balance == null || balance < params.amount) {
                emit(NetworkResource.Error("Insufficient Funds"))
                return@flow
            }
            delay(500)
            val entity = params.toTransactionEntity()
            val result = dao.insertTransaction(entity)
            if (result == -1L) {
                emit(NetworkResource.Error("Transfer Failed"))
                return@flow
            } else {
                val account = accountDao.getAccountById(params.sourceAccountNumber)
                val updatedBalance = account?.balance?:0L
                account?.let { accountDao.updateAccount(it.copy(balance = updatedBalance-params.amount)) }
                emit(NetworkResource.Success(entity))
            }

        }.onStart {
            emit(NetworkResource.Loading())
        }.catch {
            emit(NetworkResource.Error(message = "An Issue Occurred"))
        }


}