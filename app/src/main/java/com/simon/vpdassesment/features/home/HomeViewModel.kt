package com.simon.vpdassesment.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.simon.vpdassesment.core.database.AccountsDao
import com.simon.vpdassesment.core.database.TransactionsDao
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.core.network.isError
import com.simon.vpdassesment.core.network.isLoading
import com.simon.vpdassesment.core.network.isSuccess
import com.simon.vpdassesment.data.datasource.params.GetAccountsParams
import com.simon.vpdassesment.data.repositories.AccountRepository
import com.simon.vpdassesment.features.home.model.CardModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dao: AccountsDao,
    private val transactionsdao: TransactionsDao,
) : ViewModel() {

    private val  _accountState= MutableStateFlow(AccountState())
    val accountState:StateFlow<AccountState> = _accountState

    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val accounts = dao.getAllUserAccounts(userId)

    val transactions = transactionsdao.fetchPaginatedPosts()

    private fun getUserDetails():String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
            return currentUser.displayName?:currentUser.email.orEmpty()
        }
        else{
            return ""
        }

    }
}

data class AccountState(
    val accounts:List<CardModel> = emptyList(),
    val fetchingAccounts:Boolean=false,
    val transactions:List<Any> = emptyList()
)