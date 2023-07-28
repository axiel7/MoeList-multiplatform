package utils

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

expect fun LocalDateTime.format(format: String): String

object DateUtils {

    private const val ISO_DATE = "yyyy-MM-dd"
    private const val MEDIUM_FORMAT = "MMM d, uuuu"

    fun unixtimeToStringDate(
        time: Long?,
        format: String = ISO_DATE
    ): String? {
        if (time == null) return null
        return try {
            Instant.fromEpochMilliseconds(time)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(format)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @return the date in LocalDate, null if fails
     */
    fun getLocalDateFromIsoString(
        date: String?
    ): LocalDate? {
        if (date == null) return null
        return try {
            LocalDate.parse(isoString = date)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @return the date in LocalDate, null if fails
     */
    fun getLocalDateFromMillis(millis: Long): LocalDate? {
        return try {
            Instant.fromEpochMilliseconds(millis)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @return the date in unixtime, null if fails
     */
    fun getTimeInMillisFromIsoString(
        date: String?
    ): Long? {
        if (date == null) return null
        return try {
            getLocalDateFromIsoString(date)
                ?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Formats a date in a string with default format 'Jan 1, 1977'
     */
    fun formatLocalDateToString(
        date: LocalDate?,
        format: String = MEDIUM_FORMAT
    ): String? {
        if (date == null) return null
        return try {
            date.atTime(hour = 0, minute = 0).format(format)
        } catch (e: Exception) {
            null
        }
    }

    fun LocalDate?.toLocalized(
        format: String = MEDIUM_FORMAT
    ): String = try {
        this?.atTime(hour = 0, minute = 0)?.format(format) ?: ""
    } catch (e: Exception) {
        ""
    }

    fun String.parseIsoDate(): LocalDate? = try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        null
    }

    fun String.parseIsoDateAndLocalize(
        format: String = MEDIUM_FORMAT
    ): String? = try {
        when (this.count { it == '-' }) {
            0 -> this //only the year (2007)
            1 -> { //year and month (2007-11)
                this
                //TODO: replace with the following code when the bug
                // https://bugs.openjdk.org/browse/JDK-8168532 is fixed
                //YearMonth.parse(this)
                //    ?.format(DateTimeFormatter.ofLocalizedDate(style))
            }

            else -> {
                LocalDate.parse(this)
                    .atTime(hour = 0, minute = 0)
                    .format(format)
            }
        }
    } catch (e: Exception) {
        null
    }

    //fun String.toIsoFormat(inputFormat: DateTimeFormatter) =
    //    LocalDate.parse(this, inputFormat).toString()

    fun LocalDate.toEpochMillis() = atTime(hour = 0, minute = 0)
        .toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()

    fun LocalDate.withNextDayOfWeek(
        dayOfWeek: DayOfWeek,
    ): LocalDate {
        val daysDiff = this.dayOfWeek.isoDayNumber - dayOfWeek.isoDayNumber
        val daysToAdd = if (daysDiff >= 0) 7 - daysDiff else -daysDiff
        return plus(daysToAdd, DateTimeUnit.DAY)
    }

    /**
     * Converts seconds to years, months, weeks, days, hours or minutes.
     * Depending if there is enough time.
     * Eg. If days greater than 1 and less than 6, returns "x days"
     */
    @Composable
    fun Long.secondsToLegibleText(): String {
        val days = this / 86400
        return if (days > 6) {
            val weeks = this / 604800
            if (weeks > 4) {
                val months = this / 2629746
                if (months > 12) {
                    val years = this / 31556952
                    stringResource(MR.strings.num_years)
                        .replace("%s", years.toString())
                } else stringResource(MR.strings.num_months)
                    .replace("%s", months.toString())
            } else stringResource(MR.strings.num_weeks)
                .replace("%s", weeks.toString())
        } else if (days >= 1) stringResource(MR.strings.num_days)
            .replace("%s", days.toString())
        else {
            val hours = this / 3600
            if (hours >= 1) "$hours ${stringResource(MR.strings.hour_abbreviation)}"
            else {
                val minutes = (this % 3600) / 60
                "$minutes ${stringResource(MR.strings.minutes_abbreviation)}"
            }
        }
    }
}