package com.simon.vpdassesment.core.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PreferenceDatastoreManagerImpl constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferenceDatastoreManager {

    override suspend fun saveBoolean(key: PrefKey, value: Boolean?) {
        val newKey = booleanPreferencesKey(key.getKey())
        dataStore.edit { settings ->
            if (value != null) {
                settings[newKey] = value
            }
        }
    }

    override fun getBoolean(key: PrefKey, default: Boolean?): Flow<Boolean?> {
        val newKey = booleanPreferencesKey(key.getKey())
        return dataStore.data.map { preferences ->
            preferences[newKey] ?: default
        }
    }

    override suspend fun saveInteger(key: PrefKey, value: Int?) {
        val newKey = intPreferencesKey(key.getKey())
        dataStore.edit { settings ->
            if (value != null) {
                settings[newKey] = value
            }
        }
    }

    override fun getInteger(key: PrefKey, default: Int?): Flow<Int?> {
        val newKey = intPreferencesKey(key.getKey())
        return dataStore.data.map { preferences ->
            preferences[newKey] ?: default
        }
    }

    override suspend fun saveLong(key: PrefKey, value: Long?) {
        val newKey = longPreferencesKey(key.getKey())
        dataStore.edit { settings ->
            if (value != null) {
                settings[newKey] = value
            }
        }
    }

    override fun getLong(key: PrefKey, default: Long?): Flow<Long?> {
        val newKey = longPreferencesKey(key.getKey())
        return dataStore.data.map { preferences ->
            preferences[newKey] ?: default
        }
    }

    override suspend fun saveFloat(key: PrefKey, value: Float?) {
        val newKey = floatPreferencesKey(key.getKey())
        dataStore.edit { settings ->
            if (value != null) {
                settings[newKey] = value
            }
        }
    }

    override fun getFloat(key: PrefKey, default: Float?): Flow<Float?> {
        val newKey = floatPreferencesKey(key.getKey())
        return dataStore.data.map { preferences ->
            preferences[newKey] ?: default
        }
    }

    override suspend fun saveDouble(key: PrefKey, value: Double?) {
        val newKey = doublePreferencesKey(key.getKey())
        dataStore.edit { settings ->
            if (value != null) {
                settings[newKey] = value
            }
        }
    }

    override fun getDouble(key: PrefKey, default: Double?): Flow<Double?> {
        val newKey = doublePreferencesKey(key.getKey())
        return dataStore.data.map { preferences ->
            preferences[newKey] ?: default
        }
    }

    override suspend fun saveString(key: PrefKey, value: String?) {
        val newKey = stringPreferencesKey(key.getKey())
        dataStore.edit { settings ->
            if (value != null) {
                settings[newKey] = value
            }
        }
    }

    override fun getString(key: PrefKey, default: String?): Flow<String?> {
        val newKey = stringPreferencesKey(key.getKey())
        return dataStore.data.map { preferences ->
            preferences[newKey] ?: default
        }
    }

    override suspend fun saveObjectList(key: PrefKey, list: List<Any?>) {
        val newKey = stringPreferencesKey(key.getKey())
        val json = Json.encodeToString(list)

        dataStore.edit { settings ->
            settings[newKey] = json
        }
    }

    override fun <T> getObjectList(key: PrefKey, type: Class<T>): Flow<List<T>> {
        val newKey = stringPreferencesKey(key.getKey())
        val typeToken = object: TypeToken<List<T>>() {}.type
        return dataStore.data.map { preferences ->
            val json = preferences[newKey] ?: ""
            Gson().fromJson(json, typeToken)
        }
    }

    override suspend fun saveObject(key: PrefKey, obj: Any?) {
        val newKey = stringPreferencesKey(key.getKey())
        val json = obj.toJson()
        Napier.e("Storing"+json.orEmpty())

        dataStore.edit { settings ->
            settings[newKey] = json?:""
        }
    }



    override fun <T> getObject(key: PrefKey, type: Class<T>): Flow<T?> {
        val newKey = stringPreferencesKey(key.getKey())
        return dataStore.data.map { preferences ->
            val json = preferences[newKey] ?: ""
            Napier.e("getting "+json)
            if(json.isEmpty()) return@map null
            try {
                Gson().fromJson(json, type)
            }
            catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun signout() {
        dataStore.edit { settings ->
            settings.clear()
        }
    }

}

fun Any?.toJson():String?{
    return try {
        Gson().toJson(this)
    }
    catch (e:Exception){
        e.printStackTrace()
        null
    }
}