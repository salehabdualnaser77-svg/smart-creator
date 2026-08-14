package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.mikrotikDataStore: DataStore<Preferences> by preferencesDataStore(name = "mikrotik_connection_prefs")

data class MikrotikConnectionConfig(
    val host: String = "192.168.88.1",
    val port: Int = 8728,
    val username: String = "admin",
    val password: String = "",
    val autoLogin: Boolean = false,
    val useSsl: Boolean = false,
    val isRest: Boolean = false,
    val timeoutSeconds: Int = 10,
    val lastConnected: Long = 0L
)

class MikrotikDataStore(private val context: Context) {

    companion object {
        val KEY_HOST = stringPreferencesKey("router_host")
        val KEY_PORT = intPreferencesKey("router_port")
        val KEY_USERNAME = stringPreferencesKey("router_username")
        val KEY_PASSWORD = stringPreferencesKey("router_password")
        val KEY_AUTO_LOGIN = booleanPreferencesKey("router_auto_login")
        val KEY_USE_SSL = booleanPreferencesKey("router_use_ssl")
        val KEY_IS_REST = booleanPreferencesKey("router_is_rest")
        val KEY_TIMEOUT = intPreferencesKey("router_timeout")
        val KEY_LAST_CONNECTED = longPreferencesKey("router_last_connected")
    }

    val connectionConfig: Flow<MikrotikConnectionConfig> = context.mikrotikDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            MikrotikConnectionConfig(
                host = preferences[KEY_HOST] ?: "192.168.88.1",
                port = preferences[KEY_PORT] ?: 8728,
                username = preferences[KEY_USERNAME] ?: "admin",
                password = preferences[KEY_PASSWORD] ?: "",
                autoLogin = preferences[KEY_AUTO_LOGIN] ?: false,
                useSsl = preferences[KEY_USE_SSL] ?: false,
                isRest = preferences[KEY_IS_REST] ?: false,
                timeoutSeconds = preferences[KEY_TIMEOUT] ?: 10,
                lastConnected = preferences[KEY_LAST_CONNECTED] ?: 0L
            )
        }

    suspend fun saveConnectionConfig(config: MikrotikConnectionConfig) {
        context.mikrotikDataStore.edit { preferences ->
            preferences[KEY_HOST] = config.host
            preferences[KEY_PORT] = config.port
            preferences[KEY_USERNAME] = config.username
            preferences[KEY_PASSWORD] = config.password
            preferences[KEY_AUTO_LOGIN] = config.autoLogin
            preferences[KEY_USE_SSL] = config.useSsl
            preferences[KEY_IS_REST] = config.isRest
            preferences[KEY_TIMEOUT] = config.timeoutSeconds
            preferences[KEY_LAST_CONNECTED] = config.lastConnected
        }
    }

    suspend fun updateCredentials(
        host: String,
        port: Int,
        username: String,
        password: String,
        autoLogin: Boolean = false,
        useSsl: Boolean = false,
        isRest: Boolean = false
    ) {
        context.mikrotikDataStore.edit { preferences ->
            preferences[KEY_HOST] = host
            preferences[KEY_PORT] = port
            preferences[KEY_USERNAME] = username
            preferences[KEY_PASSWORD] = password
            preferences[KEY_AUTO_LOGIN] = autoLogin
            preferences[KEY_USE_SSL] = useSsl
            preferences[KEY_IS_REST] = isRest
        }
    }

    suspend fun clearCredentials() {
        context.mikrotikDataStore.edit { preferences ->
            preferences.remove(KEY_HOST)
            preferences.remove(KEY_PORT)
            preferences.remove(KEY_USERNAME)
            preferences.remove(KEY_PASSWORD)
            preferences.remove(KEY_AUTO_LOGIN)
            preferences.remove(KEY_USE_SSL)
            preferences.remove(KEY_IS_REST)
            preferences.remove(KEY_LAST_CONNECTED)
        }
    }

    suspend fun recordConnectionSuccess() {
        context.mikrotikDataStore.edit { preferences ->
            preferences[KEY_LAST_CONNECTED] = System.currentTimeMillis()
        }
    }
}
