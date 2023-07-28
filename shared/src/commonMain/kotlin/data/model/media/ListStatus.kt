package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ListStatus(val value: String) {
    @SerialName("watching")
    WATCHING("watching"),

    @SerialName("reading")
    READING("reading"),

    @SerialName("plan_to_watch")
    PTW("plan_to_watch"),

    @SerialName("plan_to_read")
    PTR("plan_to_read"),

    @SerialName("completed")
    COMPLETED("completed"),

    @SerialName("on_hold")
    ON_HOLD("on_hold"),

    @SerialName("dropped")
    DROPPED("dropped")
}

val listStatusAnimeValues =
    arrayOf(
        ListStatus.WATCHING,
        ListStatus.PTW,
        ListStatus.COMPLETED,
        ListStatus.ON_HOLD,
        ListStatus.DROPPED
    )

val listStatusMangaValues =
    arrayOf(
        ListStatus.READING,
        ListStatus.PTR,
        ListStatus.COMPLETED,
        ListStatus.ON_HOLD,
        ListStatus.DROPPED
    )

fun listStatusValues(mediaType: MediaType) =
    if (mediaType == MediaType.ANIME) listStatusAnimeValues else listStatusMangaValues

fun ListStatus.isCurrent() = this == ListStatus.WATCHING || this == ListStatus.READING

fun ListStatus.isPlanning() = this == ListStatus.PTW || this == ListStatus.PTR

@Composable
fun ListStatus.localized() = when (this) {
    ListStatus.WATCHING -> stringResource(MR.strings.watching)
    ListStatus.READING -> stringResource(MR.strings.reading)
    ListStatus.COMPLETED -> stringResource(MR.strings.completed)
    ListStatus.ON_HOLD -> stringResource(MR.strings.on_hold)
    ListStatus.DROPPED -> stringResource(MR.strings.dropped)
    ListStatus.PTW -> stringResource(MR.strings.ptw)
    ListStatus.PTR -> stringResource(MR.strings.ptr)
}

@Composable
fun String.listStatusDelocalized() = when (this) {
    stringResource(MR.strings.watching) -> ListStatus.WATCHING
    stringResource(MR.strings.reading) -> ListStatus.READING
    stringResource(MR.strings.completed) -> ListStatus.COMPLETED
    stringResource(MR.strings.on_hold) -> ListStatus.ON_HOLD
    stringResource(MR.strings.dropped) -> ListStatus.DROPPED
    stringResource(MR.strings.ptw) -> ListStatus.PTW
    stringResource(MR.strings.ptr) -> ListStatus.PTR
    else -> ListStatus.COMPLETED
}

fun ListStatus.icon() = when (this) {
    ListStatus.WATCHING, ListStatus.READING -> "play_circle_outline_24"
    ListStatus.COMPLETED -> "check_circle_outline_24"
    ListStatus.ON_HOLD -> "pause_circle_outline_24"
    ListStatus.DROPPED -> "delete_outline_24"
    ListStatus.PTW, ListStatus.PTR -> "ic_round_access_time_24"
}

