package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource

enum class MediaSort(val value: String) {
    ANIME_TITLE("anime_title"),
    ANIME_SCORE("anime_score"),
    ANIME_NUM_USERS("anime_num_list_users"),
    ANIME_START_DATE("anime_start_date"),
    SCORE("list_score"),
    UPDATED("list_updated_at"),
    MANGA_TITLE("manga_title"),
    MANGA_START_DATE("manga_start_date");

    override fun toString(): String {
        return value
    }

    companion object {
        fun forValue(value: String) = values().firstOrNull { it.value == value }
    }
}

val animeListSortItems
    get() = arrayOf(
        MediaSort.ANIME_TITLE, MediaSort.SCORE, MediaSort.UPDATED, MediaSort.ANIME_START_DATE
    )

val mangaListSortItems
    get() = arrayOf(
        MediaSort.MANGA_TITLE, MediaSort.SCORE, MediaSort.UPDATED, MediaSort.MANGA_START_DATE
    )

@Composable
fun MediaSort.localized() = when (this) {
    MediaSort.ANIME_TITLE -> stringResource(MR.strings.sort_title)
    MediaSort.ANIME_SCORE -> stringResource(MR.strings.sort_score)
    MediaSort.ANIME_NUM_USERS -> stringResource(MR.strings.members)
    MediaSort.ANIME_START_DATE -> stringResource(MR.strings.sort_start_date)
    MediaSort.SCORE -> stringResource(MR.strings.sort_score)
    MediaSort.UPDATED -> stringResource(MR.strings.sort_last_updated)
    MediaSort.MANGA_TITLE -> stringResource(MR.strings.sort_title)
    MediaSort.MANGA_START_DATE -> stringResource(MR.strings.sort_start_date)
}