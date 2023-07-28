package data.model.media

import animeCompletedListStyle
import animeCurrentListStyle
import animeDroppedListStyle
import animePausedListStyle
import animePlannedListStyle
import data.datastore.PreferencesDataStore
import mangaCompletedListStyle
import mangaCurrentListStyle
import mangaDroppedListStyle
import mangaPausedListStyle
import mangaPlannedListStyle

data class ListType(
    val status: ListStatus,
    val mediaType: MediaType,
) {
    val stylePreferenceKey
        get() = when (status) {
            ListStatus.WATCHING -> PreferencesDataStore.ANIME_CURRENT_LIST_STYLE_PREFERENCE_KEY
            ListStatus.READING -> PreferencesDataStore.MANGA_CURRENT_LIST_STYLE_PREFERENCE_KEY
            ListStatus.PTW -> PreferencesDataStore.ANIME_PLANNED_LIST_STYLE_PREFERENCE_KEY
            ListStatus.PTR -> PreferencesDataStore.MANGA_PLANNED_LIST_STYLE_PREFERENCE_KEY
            ListStatus.COMPLETED ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_COMPLETED_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_COMPLETED_LIST_STYLE_PREFERENCE_KEY

            ListStatus.ON_HOLD ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_PAUSED_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_PAUSED_LIST_STYLE_PREFERENCE_KEY

            ListStatus.DROPPED ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_DROPPED_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_DROPPED_LIST_STYLE_PREFERENCE_KEY
        }

    val styleGlobalAppVariable
        get() = when (status) {
            ListStatus.WATCHING -> animeCurrentListStyle
            ListStatus.READING -> mangaCurrentListStyle
            ListStatus.PTW -> animePlannedListStyle
            ListStatus.PTR -> mangaPlannedListStyle
            ListStatus.COMPLETED ->
                if (mediaType == MediaType.ANIME) animeCompletedListStyle
                else mangaCompletedListStyle

            ListStatus.ON_HOLD ->
                if (mediaType == MediaType.ANIME) animePausedListStyle
                else mangaPausedListStyle

            ListStatus.DROPPED ->
                if (mediaType == MediaType.ANIME) animeDroppedListStyle
                else mangaDroppedListStyle
        }
}
