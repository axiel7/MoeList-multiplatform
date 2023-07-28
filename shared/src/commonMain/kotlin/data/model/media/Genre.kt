package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Composable
fun Genre.nameLocalized() = when (this.name) {
    "Action" -> stringResource(MR.strings.genre_action)
    "Adventure" -> stringResource(MR.strings.genre_adventure)
    "Cars" -> stringResource(MR.strings.genre_cars)
    "Comedy" -> stringResource(MR.strings.genre_comedy)
    "Dementia" -> stringResource(MR.strings.genre_dementia)
    "Demons" -> stringResource(MR.strings.genre_demons)
    "Drama" -> stringResource(MR.strings.genre_drama)
    "Ecchi" -> stringResource(MR.strings.genre_ecchi)
    "Fantasy" -> stringResource(MR.strings.genre_fantasy)
    "Game" -> stringResource(MR.strings.genre_game)
    "Harem" -> stringResource(MR.strings.genre_harem)
    "Hentai" -> stringResource(MR.strings.genre_hentai)
    "Historical" -> stringResource(MR.strings.genre_historical)
    "Horror" -> stringResource(MR.strings.genre_horror)
    "Josei" -> stringResource(MR.strings.genre_josei)
    "Kids" -> stringResource(MR.strings.genre_kids)
    "Magic" -> stringResource(MR.strings.genre_magic)
    "Martial Arts" -> stringResource(MR.strings.genre_martial_arts)
    "Mecha" -> stringResource(MR.strings.genre_mecha)
    "Military" -> stringResource(MR.strings.genre_military)
    "Music" -> stringResource(MR.strings.genre_music)
    "Mystery" -> stringResource(MR.strings.genre_mystery)
    "Parody" -> stringResource(MR.strings.genre_parody)
    "Police" -> stringResource(MR.strings.genre_police)
    "Psychological" -> stringResource(MR.strings.genre_psychological)
    "Romance" -> stringResource(MR.strings.genre_romance)
    "Samurai" -> stringResource(MR.strings.genre_samurai)
    "School" -> stringResource(MR.strings.genre_school)
    "Sci-Fi" -> stringResource(MR.strings.genre_sci_fi)
    "Seinen" -> stringResource(MR.strings.genre_seinen)
    "Shoujo" -> stringResource(MR.strings.genre_shoujo)
    "Shoujo Ai" -> stringResource(MR.strings.genre_shoujo_ai)
    "Shounen" -> stringResource(MR.strings.genre_shounen)
    "Shounen Ai" -> stringResource(MR.strings.genre_shounen_ai)
    "Slice of Life" -> stringResource(MR.strings.genre_slice_of_life)
    "Space" -> stringResource(MR.strings.genre_space)
    "Sports" -> stringResource(MR.strings.genre_sports)
    "Super Power" -> stringResource(MR.strings.genre_superpower)
    "Supernatural" -> stringResource(MR.strings.genre_supernatural)
    "Thriller" -> stringResource(MR.strings.genre_thriller)
    "Vampire" -> stringResource(MR.strings.genre_vampire)
    "Yaoi" -> stringResource(MR.strings.genre_yaoi)
    "Yuri" -> stringResource(MR.strings.genre_yuri)
    else -> this.name
}