package data.repository

import api
import data.model.ApiParams
import data.model.Response
import data.model.manga.MangaDetails
import data.model.manga.MangaList
import data.model.manga.MangaRanking
import data.model.manga.MyMangaListStatus
import data.model.manga.UserMangaList
import data.model.media.RankingType
import io.ktor.http.HttpStatusCode

object MangaRepository {

    const val MANGA_DETAILS_FIELDS =
        "id,title,main_picture,pictures,alternative_titles,start_date,end_date," +
                "synopsis,mean,rank,popularity,num_list_users,num_scoring_users,media_type,status,genres," +
                "my_list_status{num_times_reread},num_chapters,num_volumes,source,authors{first_name,last_name}," +
                "serialization,related_anime{media_type},related_manga{media_type},recommendations,background"

    suspend fun getMangaDetails(
        mangaId: Int
    ): MangaDetails? {
        return try {
            api.getMangaDetails(mangaId, MANGA_DETAILS_FIELDS)
        } catch (e: Exception) {
            null
        }
    }

    const val USER_MANGA_LIST_FIELDS =
        "alternative_titles{en,ja},list_status{num_times_reread},num_chapters,num_volumes,media_type,status"

    suspend fun getUserMangaList(
        apiParams: ApiParams,
        page: String? = null
    ): Response<List<UserMangaList>>? {
        return try {
            val result = if (page == null) api.getUserMangaList(apiParams)
            else api.getUserMangaList(page)
            result.error?.let { BaseRepository.handleResponseError(it) }
            return result
        } catch (e: Exception) {
            Response(message = e.message)
        }
    }

    suspend fun updateMangaEntry(
        mangaId: Int,
        status: String?,
        score: Int?,
        chaptersRead: Int?,
        volumesRead: Int?,
        startDate: String?,
        endDate: String?,
        numRereads: Int?,
    ): MyMangaListStatus? {
        return try {
            val result = api.updateUserMangaList(
                mangaId,
                status,
                score,
                chaptersRead,
                volumesRead,
                startDate,
                endDate,
                numRereads
            )
            result.error?.let { BaseRepository.handleResponseError(it) }
            return result
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteMangaEntry(
        mangaId: Int
    ): Boolean {
        return try {
            val result = api.deleteMangaEntry(mangaId)
            return result.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    suspend fun searchManga(
        apiParams: ApiParams,
        page: String? = null
    ): Response<List<MangaList>>? {
        return try {
            val result = if (page == null) api.getMangaList(apiParams)
            else api.getMangaList(page)
            result.error?.let { BaseRepository.handleResponseError(it) }
            return result
        } catch (e: Exception) {
            Response(message = e.message)
        }
    }

    suspend fun getMangaRanking(
        rankingType: RankingType,
        apiParams: ApiParams,
        page: String? = null
    ): Response<List<MangaRanking>>? {
        return try {
            val result = if (page == null) api.getMangaRanking(apiParams, rankingType.value)
            else api.getMangaRanking(page)
            result.error?.let { BaseRepository.handleResponseError(it) }
            return result
        } catch (e: Exception) {
            Response(message = e.message)
        }
    }
}