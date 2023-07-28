package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WeekDay(val value: String) {
    @SerialName("monday")
    MONDAY("monday"),

    @SerialName("tuesday")
    TUESDAY("tuesday"),

    @SerialName("wednesday")
    WEDNESDAY("wednesday"),

    @SerialName("thursday")
    THURSDAY("thursday"),

    @SerialName("friday")
    FRIDAY("friday"),

    @SerialName("saturday")
    SATURDAY("saturday"),

    @SerialName("sunday")
    SUNDAY("sunday")
}

fun WeekDay.numeric() = when (this) {
    WeekDay.MONDAY -> 1
    WeekDay.TUESDAY -> 2
    WeekDay.WEDNESDAY -> 3
    WeekDay.THURSDAY -> 4
    WeekDay.FRIDAY -> 5
    WeekDay.SATURDAY -> 6
    WeekDay.SUNDAY -> 7
}

@Composable
fun WeekDay.localized() = when (this) {
    WeekDay.MONDAY -> stringResource(MR.strings.monday)
    WeekDay.TUESDAY -> stringResource(MR.strings.tuesday)
    WeekDay.WEDNESDAY -> stringResource(MR.strings.wednesday)
    WeekDay.THURSDAY -> stringResource(MR.strings.thursday)
    WeekDay.FRIDAY -> stringResource(MR.strings.friday)
    WeekDay.SATURDAY -> stringResource(MR.strings.saturday)
    WeekDay.SUNDAY -> stringResource(MR.strings.sunday)
}

fun WeekDay.toDayOfWeek() = when (this) {
    WeekDay.MONDAY -> DayOfWeek.MONDAY
    WeekDay.TUESDAY -> DayOfWeek.TUESDAY
    WeekDay.WEDNESDAY -> DayOfWeek.WEDNESDAY
    WeekDay.THURSDAY -> DayOfWeek.THURSDAY
    WeekDay.FRIDAY -> DayOfWeek.FRIDAY
    WeekDay.SATURDAY -> DayOfWeek.SATURDAY
    WeekDay.SUNDAY -> DayOfWeek.SUNDAY
}