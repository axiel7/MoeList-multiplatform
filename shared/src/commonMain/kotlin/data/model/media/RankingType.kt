package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.Serializable

@Serializable
enum class RankingType(val value: String) {
    SCORE("all"),
    POPULARITY("bypopularity"),
    FAVORITE("favorite"),
    UPCOMING("upcoming"),
    AIRING("airing"),
}

val rankingAnimeValues
    get() =
        arrayOf(
            RankingType.SCORE,
            RankingType.POPULARITY,
            RankingType.FAVORITE,
            RankingType.UPCOMING
        )

val rankingMangaValues
    get() =
        arrayOf(RankingType.SCORE, RankingType.POPULARITY, RankingType.FAVORITE)

@Composable
fun RankingType.localized() = when (this) {
    RankingType.SCORE -> stringResource(MR.strings.sort_score)
    RankingType.POPULARITY -> stringResource(MR.strings.popularity)
    RankingType.FAVORITE -> stringResource(MR.strings.favorite)
    RankingType.UPCOMING -> stringResource(MR.strings.upcoming)
    RankingType.AIRING -> stringResource(MR.strings.airing)
}