package com.simon.vpdassesment.features.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Index
import com.google.firebase.auth.FirebaseAuth
import com.simon.vpdassesment.core.database.AccountsDao
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.core.preference.PrefKey
import com.simon.vpdassesment.core.preference.PreferenceDatastoreManager
import com.simon.vpdassesment.data.datasource.params.GetAccountsParams
import com.simon.vpdassesment.data.datasource.params.TransferParams
import com.simon.vpdassesment.data.repositories.AccountRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferRepositoryImpl: AccountRepositoryImpl,
    private val accountsDao: AccountsDao
) : ViewModel() {

    private val _transferState = MutableStateFlow(TransferState())
    val transferState: StateFlow<TransferState> = _transferState

    val userId by lazy {
        FirebaseAuth.getInstance().currentUser?.uid
    }
    val userAccounts =
        transferRepositoryImpl.getAccounts(params = GetAccountsParams(userId.orEmpty()))

    val selectedAccount = combine(_transferState, userAccounts) { state, accounts ->
        if (state.sourceAccountNumber != null && accounts.isNotEmpty()) {
            return@combine accounts.firstOrNull { it.accountNumber == state.sourceAccountNumber }
        }
        return@combine null
    }

    // Function to initiate the transfer
    fun transferFunds(onError: (String) -> Unit, onSuccess: () -> Unit) = viewModelScope.launch {
        val state = transferState.value

        // Validate inputs before proceeding
        validateInput()

        // Check if there are any validation errors
        if (state.transferError == null && state.isValid()) {
            // Check if the amount is greater than the available balance (assuming balance is fetched elsewhere)
            val balance = getAccountBalance() // Replace with actual balance retrieval logic
            if ((state.amount ?: 0L) > balance) {
                _transferState.update { it.copy(transferError = "Insufficient balance") }
                return@launch
            }

            // Proceed with the transfer
            transferRepositoryImpl.makeTransfer(
                TransferParams(
                    sourceAccountNumber = state.sourceAccountNumber!!,
                    destinationAccountNumber = state.destinationAccountNumber!!,
                    destinationAccountName = state.destinationAccountName.orEmpty(),
                    destinationBankId = state.destinationBankId.orEmpty(),
                    amount = state.amount!!,
                    description = state.description
                )
            ).collectLatest { result ->
                when (result) {
                    is NetworkResource.Loading -> {
                        _transferState.update { it.copy(transferrring = true) }
                    }

                    is NetworkResource.Success -> {
                        _transferState.update { it.copy(transferrring = false) }
                        onSuccess()
                    }

                    is NetworkResource.Error -> {
                        _transferState.update {
                            it.copy(
                                transferrring = false,
                                transferError = result.message
                            )
                        }
                        onError(result.message)
                    }
                }
            }
        } else {
            onError(state.transferError.orEmpty())
        }
    }

    // Function to update source account number
    fun updateSourceAccountNumber(sourceAccountNumber: Int) = viewModelScope.launch {
        _transferState.update { currentState ->
            currentState.copy(
                sourceAccountNumber = userAccounts.first()
                    .getOrNull(sourceAccountNumber)?.accountNumber, transferError = null
            )
        }
        validateInput()
    }

    // Function to update destination account number
    fun updateDestinationAccountNumber(destinationAccountNumber: Long) = viewModelScope.launch {
        _transferState.update { currentState ->
            currentState.copy(
                destinationAccountNumber = destinationAccountNumber,
                transferError = null
            )
        }
        validateInput()
    }

    // Function to update destination account name
    fun updateDestinationAccountName(destinationAccountName: String) = viewModelScope.launch {
        _transferState.update { currentState ->
            currentState.copy(destinationAccountName = destinationAccountName, transferError = null)
        }
        validateInput()
    }

    // Function to update destination bank ID
    fun updateDestinationBankId(destinationBankId: String) = viewModelScope.launch {
        _transferState.update { currentState ->
            currentState.copy(destinationBankId = destinationBankId, transferError = null)
        }
        validateInput()
    }

    // Function to update transfer amount
    fun updateAmount(amount: Long) = viewModelScope.launch {
        _transferState.update { currentState ->
            currentState.copy(amount = amount, transferError = null)
        }
        validateInput()
    }

    // Function to update transfer description
    fun updateDescription(description: String) = viewModelScope.launch {
        _transferState.update { currentState ->
            currentState.copy(description = description, transferError = null)
        }
        validateInput()
    }

    private suspend fun validateInput() {
        val currentState = _transferState.value

        var transferError = when {
            currentState.sourceAccountNumber == null -> null // Consider null as valid for validation
            currentState.sourceAccountNumber.toString().length < 5 -> "Source account number must be at least 5 digits"
            currentState.destinationAccountNumber == null -> null // Consider null as valid for validation
            currentState.destinationAccountNumber.toString().length < 5 -> "Destination account number must be at least 5 digits"
            currentState.destinationAccountName.isNullOrEmpty() -> "Destination account name cannot be empty"
            currentState.destinationBankId.isNullOrEmpty() -> "Destination bank ID cannot be empty"
            currentState.amount == null -> null // Consider null as valid for validation
            currentState.amount <= 0 -> "Amount must be greater than zero"
            currentState.description.isBlank() -> "Description cannot be empty"
            else -> null
        }
        if (currentState.sourceAccountNumber != null && currentState.amount != null) {
            val currentBalance = getAccountBalance()
            if (currentBalance < currentState.amount) {
                transferError = "Insufficient funds"
            }
        }

        _transferState.update {
            it.copy(
                transferError = transferError,
                destinationAccountName = if (it.destinationAccountNumber != null && it.destinationBankId.isNullOrEmpty()
                        .not()
                ) "Tule Simon Joseph" else null
            )
        }
    }

    // Function to get account balance (replace with actual logic)
    private suspend fun getAccountBalance(): Long {
        val source = _transferState.value.sourceAccountNumber ?: return 0L
        val balance =
            accountsDao.getAccountById(source)?.balance ?: 0L
        // Replace with actual balance retrieval logic
        return balance // Example balance
    }
}