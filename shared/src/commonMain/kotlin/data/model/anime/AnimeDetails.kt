package data.model.anime

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import data.model.manga.RelatedManga
import data.model.media.AlternativeTitles
import data.model.media.BaseMediaDetails
import data.model.media.Genre
import data.model.media.MainPicture
import data.model.media.Statistics
import data.model.media.localized
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDetails(
    override val id: Int = 0,
    override val title: String? = null,
    @SerialName("main_picture")
    override val mainPicture: MainPicture? = null,
    @SerialName("alternative_titles")
    override val alternativeTitles: AlternativeTitles? = null,
    @SerialName("start_date")
    override val startDate: String? = null,
    @SerialName("end_date")
    override val endDate: String? = null,
    override val synopsis: String? = null,
    override val mean: Float? = null,
    override val rank: Int? = null,
    override val popularity: Int? = null,
    @SerialName("num_list_users")
    override val numListUsers: Int? = null,
    @SerialName("num_scoring_users")
    override val numScoringUsers: Int? = null,
    override val nsfw: String? = null,
    @SerialName("created_at")
    override val createdAt: String? = null,
    @SerialName("updated_at")
    override val updatedAt: String? = null,
    @SerialName("media_type")
    override val mediaType: String? = null,
    override val status: String? = null,
    override val genres: List<Genre>? = null,
    override val pictures: List<MainPicture>? = null,
    override val background: String? = null,
    @SerialName("related_anime")
    override val relatedAnime: List<RelatedAnime>? = null,
    @SerialName("related_manga")
    override val relatedManga: List<RelatedManga>? = null,
    override val recommendations: List<Recommendations<AnimeNode>>? = null,
    @SerialName("my_list_status")
    override val myListStatus: MyAnimeListStatus? = null,
    @SerialName("num_episodes")
    val numEpisodes: Int? = null,
    @SerialName("start_season")
    val startSeason: StartSeason? = null,
    @SerialName("broadcast")
    val broadcast: Broadcast? = null,
    @SerialName("source")
    val source: String? = null,
    @SerialName("average_episode_duration")
    val averageEpisodeDuration: Int? = null,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("studios")
    val studios: List<Studio>? = null,
    @SerialName("opening_themes")
    val openingThemes: List<Theme>? = null,
    @SerialName("ending_themes")
    val endingThemes: List<Theme>? = null,
    @SerialName("statistics")
    val statistics: Statistics? = null,
) : BaseMediaDetails()

fun AnimeDetails.toAnimeNode() = AnimeNode(
    id = id,
    title = title ?: "",
    alternativeTitles = alternativeTitles,
    mainPicture = mainPicture,
    startSeason = startSeason,
    numEpisodes = numEpisodes,
    numListUsers = numListUsers,
    mediaType = mediaType,
    status = status,
    mean = mean,
)

@Composable
fun AnimeDetails.sourceLocalized() = when (this.source) {
    "original" -> stringResource(MR.strings.original)
    "manga" -> stringResource(MR.strings.manga)
    "novel" -> stringResource(MR.strings.novel)
    "light_novel" -> stringResource(MR.strings.light_novel)
    "visual_novel" -> stringResource(MR.strings.visual_novel)
    "game" -> stringResource(MR.strings.game)
    "web_manga" -> stringResource(MR.strings.web_manga)
    "music" -> stringResource(MR.strings.music)
    "4_koma_manga" -> "4-Koma ${stringResource(MR.strings.manga)}"
    else -> this.source
}

@Composable
fun AnimeDetails.broadcastTimeText() = buildString {
    if (broadcast?.dayOfTheWeek != null) {
        append(broadcast.dayOfTheWeek.localized())
        append(" ")
        if (broadcast.startTime != null) {
            append(broadcast.startTime)
            append(" (JST)")
        }
    } else append(stringResource(MR.strings.unknown))
}

@Composable
fun AnimeDetails.episodeDurationLocalized() =
    if (averageEpisodeDuration != null && averageEpisodeDuration > 0) {
        if (averageEpisodeDuration >= 60) {
            if (averageEpisodeDuration >= 3600) {
                "${averageEpisodeDuration / 3600} ${stringResource(MR.strings.hour_abbreviation)}"
            } else "${averageEpisodeDuration / 60} ${stringResource(MR.strings.minutes_abbreviation)}"
        } else "<1 ${stringResource(MR.strings.minutes_abbreviation)}"
    } else stringResource(MR.strings.unknown)