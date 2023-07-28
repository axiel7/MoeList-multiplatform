package data.model.anime

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import data.model.media.WeekDay
import data.model.media.localized
import data.model.media.toDayOfWeek
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.DateUtils.secondsToLegibleText
import utils.DateUtils.withNextDayOfWeek
import utils.SeasonCalendar
import utils.format
import kotlin.math.absoluteValue

@Serializable
data class Broadcast(
    @SerialName("day_of_the_week")
    val dayOfTheWeek: WeekDay? = null,
    @SerialName("start_time")
    val startTime: String? = null
)

@Composable
fun Broadcast?.dayTimeText() = buildString {
    if (this@dayTimeText != null) {
        if (dayOfTheWeek != null) append(dayOfTheWeek.localized())
        if (startTime != null) append(" $startTime")
        if (dayOfTheWeek == null && startTime == null)
            append(stringResource(MR.strings.unknown))
    } else append(stringResource(MR.strings.unknown))
}

@Composable
fun Broadcast.remainingText() = if (startTime != null && dayOfTheWeek != null) {
    val remaining = remaining()
    if (remaining > 0) remaining.secondsToLegibleText()
    else remaining.absoluteValue.secondsToLegibleText()
} else ""

@Composable
fun Broadcast.airingInString() = if (startTime != null && dayOfTheWeek != null) {
    val remaining = remaining()
    if (remaining > 0) {
        stringResource(MR.strings.airing_in)
            .replace("%s", remaining.secondsToLegibleText())
    } else stringResource(MR.strings.aired_ago)
        .replace("%s", remaining.absoluteValue.secondsToLegibleText())
} else ""

fun Broadcast.nextAiringDayFormatted() =
    dateTimeUntilNextBroadcast()?.format("EE, d MMM HH:mm")

fun Broadcast.remaining() =
    secondsUntilNextBroadcast() - Clock.System.now().epochSeconds

fun Broadcast.secondsUntilNextBroadcast() =
    dateTimeUntilNextBroadcast()?.toInstant(TimeZone.currentSystemDefault())?.epochSeconds ?: 0

fun Broadcast.dateTimeUntilNextBroadcast(): LocalDateTime? =
    if (startTime != null && dayOfTheWeek != null) {
        val airingDay = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .withNextDayOfWeek(dayOfTheWeek.toDayOfWeek())
        val airingTime = LocalTime.parse(startTime) //TODO check if works with "23:00" format

        airingTime
            .atDate(airingDay)
            .toInstant(SeasonCalendar.japanTimeZone)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    } else null
