package ui.home

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.coroutineScope
import data.model.ApiParams
import data.model.anime.AnimeList
import data.model.anime.AnimeRanking
import data.model.anime.AnimeSeasonal
import data.model.media.MediaSort
import data.model.media.RankingType
import data.repository.AnimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import nsfw
import ui.base.BaseViewModel
import utils.Constants
import utils.SeasonCalendar
import utils.SeasonCalendar.toWeekDay

class HomeViewModel : BaseViewModel() {

    fun initRequestChain(isLoggedIn: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            isLoading = true
            if (todayAnimes.isEmpty()) getTodayAiringAnimes()
            if (seasonAnimes.isEmpty()) getSeasonAnimes()
            if (isLoggedIn && recommendedAnimes.isEmpty()) getRecommendedAnimes()
            isLoading = false
        }
    }

    private val paramsToday = ApiParams(
        sort = MediaSort.ANIME_SCORE.value,
        nsfw = nsfw,
        fields = AnimeRepository.TODAY_FIELDS,
        limit = 100
    )
    val todayAnimes = mutableStateListOf<AnimeRanking>()
    private suspend fun getTodayAiringAnimes() {
        val result = AnimeRepository.getAnimeRanking(
            apiParams = paramsToday,
            rankingType = RankingType.AIRING
        )
        if (result?.data != null) {
            val tempList = mutableListOf<AnimeRanking>()
            for (anime in result.data) {
                if (anime.node.broadcast != null
                    && !todayAnimes.contains(anime)
                    && anime.node.broadcast.dayOfTheWeek == SeasonCalendar.japanWeekDay.toWeekDay()
                    && anime.node.status == Constants.STATUS_AIRING
                ) {
                    tempList.add(anime)
                }
            }
            tempList.sortByDescending { it.node.broadcast?.startTime }
            todayAnimes.clear()
            todayAnimes.addAll(tempList)
        } else {
            setErrorMessage(result?.message ?: result?.error ?: GENERIC_ERROR)
        }
    }

    private val paramsSeasonal = ApiParams(
        sort = MediaSort.ANIME_START_DATE.value,
        nsfw = nsfw,
        fields = "alternative_titles{en,ja},mean",
        limit = 25
    )

    val seasonAnimes = mutableStateListOf<AnimeSeasonal>()
    private suspend fun getSeasonAnimes() {
        val currentStartSeason = SeasonCalendar.currentStartSeason
        val result = AnimeRepository.getSeasonalAnimes(
            apiParams = paramsSeasonal,
            year = currentStartSeason.year,
            season = currentStartSeason.season
        )
        if (result?.data != null) {
            seasonAnimes.clear()
            seasonAnimes.addAll(result.data)
        } else {
            setErrorMessage(result?.message ?: result?.error ?: GENERIC_ERROR)
        }
    }

    private val paramsRecommended = ApiParams(
        nsfw = nsfw,
        fields = AnimeRepository.RECOMMENDED_FIELDS,
        limit = 25
    )

    val recommendedAnimes = mutableStateListOf<AnimeList>()
    private suspend fun getRecommendedAnimes() {
        val result = AnimeRepository.getRecommendedAnimes(paramsRecommended)
        if (result?.data != null) {
            recommendedAnimes.clear()
            recommendedAnimes.addAll(result.data)
        } else {
            setErrorMessage(result?.message ?: result?.error ?: GENERIC_ERROR)
        }
    }
}