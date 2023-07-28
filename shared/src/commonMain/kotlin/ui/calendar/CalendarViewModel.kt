package ui.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.coroutineScope
import data.model.ApiParams
import data.model.anime.AnimeSeasonal
import data.model.media.MediaSort
import data.model.media.numeric
import data.repository.AnimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import nsfw
import ui.base.BaseViewModel
import utils.SeasonCalendar

class CalendarViewModel : BaseViewModel() {

    private val params = ApiParams(
        sort = MediaSort.ANIME_NUM_USERS.value,
        nsfw = nsfw,
        fields = AnimeRepository.CALENDAR_FIELDS,
        limit = 300
    )

    var weekAnime by mutableStateOf(
        arrayOf<MutableList<AnimeSeasonal>>(
            mutableListOf(),//0: MONDAY
            mutableListOf(),//1: TUESDAY
            mutableListOf(),//2: WEDNESDAY
            mutableListOf(),//3: THURSDAY
            mutableListOf(),//4: FRIDAY
            mutableListOf(),//5: SATURDAY
            mutableListOf(),//6: SUNDAY
        )
    )

    fun getSeasonAnime() {
        coroutineScope.launch(Dispatchers.IO) {
            isLoading = true
            val result = AnimeRepository.getSeasonalAnimes(
                apiParams = params,
                year = SeasonCalendar.year,
                season = SeasonCalendar.currentSeason
            )

            if (result?.data == null || result.message != null) {
                setErrorMessage(result?.message ?: "Generic error")
            } else {
                result.data.forEach { anime ->
                    anime.node.broadcast?.dayOfTheWeek?.let { day ->
                        weekAnime[day.numeric() - 1].add(anime)
                    }
                }
                weekAnime = weekAnime.copyOf()
            }
            isLoading = false
        }
    }
}