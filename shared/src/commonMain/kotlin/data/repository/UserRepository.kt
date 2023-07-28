package data.repository

import api
import data.model.Response
import data.model.User
import data.model.UserStats
import jikanApi

object UserRepository {

    private const val USER_FIELDS = "id,name,gender,location,joined_at,anime_statistics"

    suspend fun getMyUser(): User? {
        return try {
            val result = api.getUser(USER_FIELDS)
            result.error?.let { BaseRepository.handleResponseError(it) }
            return result
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserStats(
        username: String
    ): Response<UserStats>? {
        return try {
            jikanApi.getUserStats(username)
        } catch (e: Exception) {
            null
        }
    }
}