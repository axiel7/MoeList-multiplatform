package data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import private.ClientId

class KtorClient(private val authToken: String?) {

    val ktorHttpClient = HttpClient {

        expectSuccess = false

        install(ContentNegotiation) {
            json(Json {
                coerceInputValues = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpCache)

        install(HttpTimeout) {
            socketTimeoutMillis = Int.MAX_VALUE.toLong()
            connectTimeoutMillis = Int.MAX_VALUE.toLong()
            requestTimeoutMillis = Int.MAX_VALUE.toLong()
        }

        /*if (BuildConfig.IS_DEBUG) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Logger Ktor =>", message)
                    }
                }
                level = LogLevel.ALL
            }
        }*/

        install(DefaultRequest) {
            header("X-MAL-CLIENT-ID", ClientId.CLIENT_ID)
            authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }
}