package data.model.anime

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartSeason(
    @SerialName("year")
    val year: Int,
    @SerialName("season")
    val season: Season
)

@Composable
fun StartSeason?.seasonYearText() = buildString {
    if (this@seasonYearText?.season != null) {
        append(season.localized())
        append(" ")
    }
    if (this@seasonYearText?.year != null) {
        append(year)
    } else append(stringResource(MR.strings.unknown))
}