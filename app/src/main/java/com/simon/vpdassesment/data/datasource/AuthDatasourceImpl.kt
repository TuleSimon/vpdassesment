package com.simon.vpdassesment.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.simon.vpdassesment.core.entity.LoginEntity
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.params.CreateAccountParams
import com.simon.vpdassesment.data.datasource.params.LoginParams
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDatasourceImpl @Inject constructor() : AuthDatasource {


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
                    LoginEntity(user.displayName.orEmpty(), user.email.orEmpty(), user.uid)
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