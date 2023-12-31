package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import data.model.anime.AnimeNode
import data.model.anime.NodeSeasonal
import data.model.manga.MangaNode
import dev.icerock.moko.resources.compose.stringResource
import titleLanguage
import utils.NumExtensions.toStringPositiveValueOrNull

abstract class BaseMediaNode {
    abstract val id: Int
    abstract val title: String
    abstract val alternativeTitles: AlternativeTitles?
    abstract val mainPicture: MainPicture?
    open val numListUsers: Int? = null
    abstract val mediaType: String?
    abstract val status: String?
    abstract val mean: Float?
}

fun BaseMediaNode.userPreferredTitle() = title(titleLanguage)

fun BaseMediaNode.title(language: TitleLanguage) = when (language) {
    TitleLanguage.ROMAJI -> title
    TitleLanguage.ENGLISH ->
        if (alternativeTitles?.en.isNullOrBlank()) title
        else alternativeTitles?.en ?: title

    TitleLanguage.JAPANESE ->
        if (alternativeTitles?.ja.isNullOrBlank()) title
        else alternativeTitles?.ja ?: title
}

fun BaseMediaNode.totalDuration() = when (this) {
    is AnimeNode -> this.numEpisodes
    is MangaNode -> this.numChapters
    is NodeSeasonal -> this.numEpisodes
    else -> null
}

@Composable
fun BaseMediaNode.durationText() = when (this) {
    is AnimeNode, is NodeSeasonal -> {
        val stringValue = this.totalDuration().toStringPositiveValueOrNull()
        if (stringValue == null) "??"
        else "$stringValue ${stringResource(MR.strings.episodes)}"
    }

    is MangaNode -> {
        val stringValue = this.numChapters.toStringPositiveValueOrNull()
        if (stringValue == null) "??"
        else "$stringValue ${stringResource(MR.strings.chapters)}"
    }

    else -> "??"
}