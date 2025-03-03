package com.simon.vpdassesment.core.network

import kotlinx.coroutines.flow.Flow

sealed interface NetworkResource<out T> {


    data class Loading<T>(val progress:Float = 0f):NetworkResource<T>
    data class Success<T>(val data: T): NetworkResource<T>
    data class Error<T>(val message: String): NetworkResource<T>
}

fun NetworkResource<Any>.isLoading():Boolean{
    return this is NetworkResource.Loading
}

fun NetworkResource<Any>.isSuccess():Boolean{
    return this is NetworkResource.Success
}

fun NetworkResource<Any>.isError():Boolean{
    return this is NetworkResource.Error
}