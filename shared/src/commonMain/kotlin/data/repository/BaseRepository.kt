package data.repository

import androidx.datastore.preferences.core.edit
import api
import createKtorClient
import data.datastore.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.REFRESH_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.getDataStore
import data.datastore.PreferencesDataStore.getValueSync
import private.ClientId

object BaseRepository {
    suspend fun handleResponseError(error: String) {
        when (error) {
            "invalid_token" -> {
                val refreshToken = getDataStore().getValueSync(REFRESH_TOKEN_PREFERENCE_KEY)
                if (refreshToken != null) {
                    try {
                        val newToken = api.getAccessToken(
                            clientId = ClientId.CLIENT_ID,
                            refreshToken = refreshToken
                        )
                        getDataStore().edit {
                            it[ACCESS_TOKEN_PREFERENCE_KEY] = newToken.accessToken!!
                            it[REFRESH_TOKEN_PREFERENCE_KEY] = newToken.refreshToken!!
                        }
                        createKtorClient(newToken.accessToken!!)
                    } catch (e: Exception) {
                        deleteAccessToken()
                    }
                } else {
                    deleteAccessToken()
                }
            }
        }
    }

    private suspend fun deleteAccessToken() {
        getDataStore().edit {
            it.remove(ACCESS_TOKEN_PREFERENCE_KEY)
            it.remove(REFRESH_TOKEN_PREFERENCE_KEY)
        }
    }
}