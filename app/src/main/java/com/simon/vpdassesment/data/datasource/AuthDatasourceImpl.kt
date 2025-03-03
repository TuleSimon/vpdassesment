package com.simon.vpdassesment.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.simon.vpdassesment.core.database.AccountsDao
import com.simon.vpdassesment.core.entity.LoginEntity
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.params.CreateAccountParams
import com.simon.vpdassesment.data.datasource.params.LoginParams
import com.simon.vpdassesment.features.home.model.CardModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.random.Random

class AuthDatasourceImpl @Inject constructor(
    private val accountsDao: AccountsDao
) : AuthDatasource {


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun login(params: LoginParams): Flow<NetworkResource<LoginEntity>> = flow {
        emit(NetworkResource.Loading())
        val tasks =
            FirebaseAuth.getInstance().signInWithEmailAndPassword(params.email, params.password)
        val result = tasks
            .await()

        if (result != null) {
            val user = result.user
            if (user != null) {
                val userEntity =
                    LoginEntity(
                        user.displayName ?: user.email.orEmpty(),
                        user.email.orEmpty(),
                        user.uid
                    )
                val cards = listOf(
                    CardModel(
                        userName = userEntity.userName,
                        accountNumber = generateAccountNumber(),
                        balance = generateAccountBalance(),
                        userId = userEntity.userId
                    ),
                    CardModel(
                        userName = userEntity.userName,
                        accountNumber = generateAccountNumber(),
                        balance = generateAccountBalance(),
                        userId = userEntity.userId
                    ),
                    CardModel(
                        userName = userEntity.userName,
                        accountNumber = generateAccountNumber(),
                        balance = generateAccountBalance(),
                        userId = userEntity.userId
                    ),
                )
                accountsDao.insertAccounts(cards)
                emit(NetworkResource.Success(userEntity))
            }
        } else {
            tasks.exception?.printStackTrace()
            emit(NetworkResource.Error("Couldn't' log in"))
        }
    }.catch {
        emit(NetworkResource.Error("Couldn't' log in"))
    }

    override fun createAccount(params: CreateAccountParams): Flow<NetworkResource<LoginEntity>> =
        flow {
            emit(NetworkResource.Loading())
            val tasks =
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(params.email, params.password)
                    .await()

            if (tasks != null) {
                val signIn =
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(params.email, params.password)
                        .await()
                if (signIn != null) {
                    FirebaseAuth.getInstance().currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(params.name).build()
                    )?.await()
                    val user = tasks.user
                    if (user != null) {
                        val userEntity =
                            LoginEntity(user.displayName.orEmpty(), user.email.orEmpty(), user.uid)
                        val cards = listOf(
                            CardModel(
                                userName = userEntity.userName,
                                accountNumber = generateAccountNumber(),
                                balance = generateAccountBalance(),
                                userId = userEntity.userId
                            ),
                            CardModel(
                                userName = userEntity.userName,
                                accountNumber = generateAccountNumber(),
                                balance = generateAccountBalance(),
                                userId = userEntity.userId
                            ),
                            CardModel(
                                userName = userEntity.userName,
                                accountNumber = generateAccountNumber(),
                                balance = generateAccountBalance(),
                                userId = userEntity.userId
                            ),
                        )
                        accountsDao.insertAccounts(cards)
                        emit(NetworkResource.Success(userEntity))
                    }
                }
            } else {
                emit(NetworkResource.Error("Couldn't' create account"))
            }
        }.catch {
            emit(NetworkResource.Error("Couldn't' create account "))
        }

}

private fun generateAccountNumber(minDigits: Int = 8, maxDigits: Int = 12): Long {
    require(minDigits >= 8) { "Account number must have at least 8 digits." }
    require(maxDigits >= minDigits) { "Max digits must be greater than or equal to min digits." }

    val length = Random.nextInt(minDigits, maxDigits + 1)
    val accountNumber = StringBuilder()

    for (i in 1..length) {
        val digit = Random.nextInt(0, 10) // Random digit between 0 and 9
        accountNumber.append(digit)
    }

    return accountNumber.toString().toLong()
}

private fun generateAccountBalance(
    minBalance: Long = 200_000,
    maxBalance: Long = 1_000_000
): Long {
    require(minBalance >= 200_000) { "Account balance must be at least 200,000." }
    require(maxBalance >= minBalance) { "Max balance must be greater than or equal to min balance." }

    return Random.nextLong(minBalance, maxBalance + 1)
}