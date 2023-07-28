package data.repository

import androidx.datastore.preferences.core.edit
import api
import createKtorClient
import data.datastore.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.REFRESH_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.getDataStore
import data.model.AccessToken
import data.model.Response
import private.ClientId
import utils.Constants
import utils.PkceGenerator

object LoginRepository {

    const val STATE = "MoeList123"
    private const val GRANT_TYPE = "authorization_code"
    private val codeVerifier = PkceGenerator.generateVerifier(length = 128)
    val loginUrl =
        "${Constants.MAL_OAUTH2_URL}authorize?response_type=code&client_id=${ClientId.CLIENT_ID}&code_challenge=$codeVerifier&state=$STATE"

    suspend fun getAccessToken(code: String): Response<AccessToken> {
        val accessToken = try {
            api.getAccessToken(
                clientId = ClientId.CLIENT_ID,
                code = code,
                codeVerifier = codeVerifier,
                grantType = GRANT_TYPE
            )
        } catch (e: Exception) {
            null
        }

        return if (accessToken?.accessToken == null)
            Response(message = "Token was null: ${accessToken?.error}: ${accessToken?.message}")
        else {
            getDataStore().edit {
                it[ACCESS_TOKEN_PREFERENCE_KEY] = accessToken.accessToken
                it[REFRESH_TOKEN_PREFERENCE_KEY] = accessToken.refreshToken!!
            }
            createKtorClient(accessToken = accessToken.accessToken)
            Response(data = accessToken)
        }
    }

}