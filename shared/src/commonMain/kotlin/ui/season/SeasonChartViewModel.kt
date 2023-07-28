package ui.season

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.coroutineScope
import data.model.ApiParams
import data.model.anime.AnimeSeasonal
import data.model.anime.Season
import data.model.anime.StartSeason
import data.model.media.MediaSort
import data.repository.AnimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import nsfw
import ui.base.BaseViewModel
import utils.SeasonCalendar

class SeasonChartViewModel : BaseViewModel() {

    var season by mutableStateOf(SeasonCalendar.currentStartSeason)
        private set

    fun setSeason(
        season: Season? = null,
        year: Int? = null
    ) {
        when {
            season != null && year != null -> this.season = StartSeason(year, season)
            season != null -> this.season = this.season.copy(season = season)
            year != null -> this.season = this.season.copy(year = year)
        }
    }

    val years = ((SeasonCalendar.year + 1) downTo BASE_YEAR).toList()

    private val params = ApiParams(
        sort = MediaSort.ANIME_NUM_USERS.value,
        nsfw = nsfw,
        fields = AnimeRepository.SEASONAL_FIELDS
    )

    val animes = mutableStateListOf<AnimeSeasonal>()
    var nextPage: String? = null
    var hasNextPage = false

    fun getSeasonalAnime(page: String? = null) {
        coroutineScope.launch(Dispatchers.IO) {
            if (page == null) {
                isLoading = true
                nextPage = null
                hasNextPage = false
            }
            val result = AnimeRepository.getSeasonalAnimes(
                apiParams = params,
                year = season.year,
                season = season.season,
                page = page
            )

            if (result?.data != null) {
                if (page == null) animes.clear()
                animes.addAll(result.data)

                nextPage = result.paging?.next
                hasNextPage = nextPage != null
            } else {
                setErrorMessage(result?.message ?: "Generic error")
                hasNextPage = false
            }
            isLoading = false
        }
    }

    companion object {
        const val BASE_YEAR = 1917
    }
}