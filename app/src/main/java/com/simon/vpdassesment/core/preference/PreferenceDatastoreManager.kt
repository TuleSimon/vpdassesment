package com.simon.vpdassesment.core.preference


import kotlinx.coroutines.flow.Flow

interface PreferenceDatastoreManager {
    suspend fun saveBoolean(key: PrefKey, value: Boolean?)
    fun getBoolean(key: PrefKey, default: Boolean? = null): Flow<Boolean?>

    suspend fun saveInteger(key: PrefKey, value: Int?)
    fun getInteger(key: PrefKey, default: Int? = null): Flow<Int?>

    suspend fun saveLong(key: PrefKey, value: Long?)
    fun getLong(key: PrefKey, default: Long? = null): Flow<Long?>

    suspend fun saveFloat(key: PrefKey, value: Float?)
    fun getFloat(key: PrefKey, default: Float? = null): Flow<Float?>

    suspend fun saveDouble(key: PrefKey, value: Double?)
    fun getDouble(key: PrefKey, default: Double? = null): Flow<Double?>

    suspend fun saveString(key: PrefKey, value: String?)
    fun getString(key: PrefKey, default: String? = null): Flow<String?>

    suspend fun saveObjectList(key: PrefKey, list: List<Any?>)
    fun <T>getObjectList(key: PrefKey, type: Class<T>): Flow<List<T>>

    suspend fun saveObject(key: PrefKey, obj: Any?)
    fun <T> getObject(key: PrefKey, type: Class<T>): Flow<T?>

    suspend fun signout()
}