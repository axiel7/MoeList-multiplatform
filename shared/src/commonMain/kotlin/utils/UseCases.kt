package utils

import androidx.datastore.preferences.core.edit
import createKtorClient
import data.datastore.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.REFRESH_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.getDataStore

suspend fun logOut() {
    getDataStore().edit {
        it.remove(ACCESS_TOKEN_PREFERENCE_KEY)
        it.remove(REFRESH_TOKEN_PREFERENCE_KEY)
    }
    createKtorClient(null)
}