package data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.moelist.common.MR
import data.model.BaseResponse
import data.model.anime.AnimeDetails
import data.model.anime.Recommendations
import data.model.anime.RelatedAnime
import data.model.manga.MangaDetails
import data.model.manga.RelatedManga
import dev.icerock.moko.resources.compose.stringResource
import utils.NumExtensions.toStringPositiveValueOrNull

abstract class BaseMediaDetails : BaseResponse() {
    abstract val id: Int
    abstract val title: String?
    abstract val mainPicture: MainPicture?
    abstract val alternativeTitles: AlternativeTitles?
    abstract val startDate: String?
    abstract val endDate: String?
    abstract val synopsis: String?
    abstract val mean: Float?
    abstract val rank: Int?
    abstract val popularity: Int?
    abstract val numListUsers: Int?
    abstract val numScoringUsers: Int?
    abstract val nsfw: String?
    abstract val createdAt: String?
    abstract val updatedAt: String?
    abstract val mediaType: String?
    abstract val status: String?
    abstract val genres: List<Genre>?
    abstract val pictures: List<MainPicture>?
    abstract val background: String?
    abstract val relatedAnime: List<RelatedAnime>?
    abstract val relatedManga: List<RelatedManga>?
    abstract val recommendations: List<Recommendations<*>>?

    abstract val myListStatus: BaseMyListStatus?

    override val error: String? = null
    override val message: String? = null
}

//fun BaseMediaDetails.userPreferredTitle() = title(App.titleLanguage)

fun BaseMediaDetails.title(language: TitleLanguage) = when (language) {
    TitleLanguage.ROMAJI -> title
    TitleLanguage.ENGLISH ->
        if (alternativeTitles?.en.isNullOrBlank()) title
        else alternativeTitles?.en ?: title

    TitleLanguage.JAPANESE ->
        if (alternativeTitles?.ja.isNullOrBlank()) title
        else alternativeTitles?.ja ?: title
}

@Composable
fun BaseMediaDetails.durationText() = when (this) {
    is AnimeDetails -> {
        val stringValue = numEpisodes.toStringPositiveValueOrNull()
        if (stringValue == null) stringResource(MR.strings.unknown)
        else "$stringValue ${stringResource(MR.strings.episodes)}"
    }

    is MangaDetails -> {
        val stringValue = numChapters.toStringPositiveValueOrNull()
        if (stringValue == null) stringResource(MR.strings.unknown)
        else "$stringValue ${stringResource(MR.strings.chapters)}"
    }

    else -> stringResource(MR.strings.unknown)
}

/**
 * @return the total num of episodes or chapters
 */
fun BaseMediaDetails.totalDuration() = when (this) {
    is AnimeDetails -> numEpisodes
    is MangaDetails -> numChapters
    else -> null
}

@Composable
fun String.mediaFormatLocalized() = when (this) {
    "tv" -> stringResource(MR.strings.tv)
    "ova" -> stringResource(MR.strings.ova)
    "ona" -> stringResource(MR.strings.ona)
    "movie" -> stringResource(MR.strings.movie)
    "special" -> stringResource(MR.strings.special)
    "music" -> stringResource(MR.strings.music)
    "unknown" -> stringResource(MR.strings.unknown)
    "manga" -> stringResource(MR.strings.manga)
    "one_shot" -> stringResource(MR.strings.one_shot)
    "manhwa" -> stringResource(MR.strings.manhwa)
    "manhua" -> stringResource(MR.strings.manhua)
    "novel" -> stringResource(MR.strings.novel)
    "light_novel" -> stringResource(MR.strings.light_novel)
    "doujinshi" -> stringResource(MR.strings.doujinshi)
    else -> this
}

@Composable
fun String.statusLocalized() = when (this) {
    "currently_airing" -> stringResource(MR.strings.airing)
    "finished_airing" -> stringResource(MR.strings.finished)
    "not_yet_aired" -> stringResource(MR.strings.not_yet_aired)
    "currently_publishing" -> stringResource(MR.strings.publishing)
    "finished" -> stringResource(MR.strings.finished)
    "on_hiatus" -> stringResource(MR.strings.on_hiatus)
    "discontinued" -> stringResource(MR.strings.discontinued)
    else -> this
}

@Composable
fun BaseMediaDetails.rankText() = if (rank == null) "N/A" else "#$rank"

@Composable
fun BaseMediaDetails.synonymsJoined(): String? {
    val joined = alternativeTitles?.synonyms?.joinToString(",\n")
    return if (joined?.isNotBlank() == true) joined
    else null
}

@Composable
fun BaseMediaDetails.synopsisAndBackground() = buildAnnotatedString {
    val hasSynopsis = !synopsis.isNullOrBlank()
    if (hasSynopsis) append(synopsis)
    if (!background.isNullOrBlank()) {
        if (hasSynopsis) append("\n\n")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(MR.strings.synopsis_background))
        }
        append("\n$background")
    }
}
