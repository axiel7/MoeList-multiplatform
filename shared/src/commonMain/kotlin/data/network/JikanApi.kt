package data.network

import data.model.Response
import data.model.UserStats
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import utils.Constants.JIKAN_API_URL

class JikanApi(private val client: HttpClient) {

    private fun HttpRequestBuilder.removeOfficialApiHeaders() = headers.apply {
        remove(HttpHeaders.Authorization)
        remove("X-MAL-CLIENT-ID")
    }

    suspend fun getUserStats(
        username: String
    ): Response<UserStats> = client.get("${JIKAN_API_URL}users/$username/statistics") {
        removeOfficialApiHeaders()
    }.body()
}