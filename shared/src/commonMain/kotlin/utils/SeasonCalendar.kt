package utils

import data.model.anime.Season
import data.model.anime.StartSeason
import data.model.media.WeekDay
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object SeasonCalendar {
    private val currentMoment get() = Clock.System.now()

    val japanTimeZone = TimeZone.of("Asia/Tokyo")

    private val localDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    private val japanDateTime = currentMoment.toLocalDateTime(japanTimeZone)

    /**
     * The current month from 0 to 11
     */
    val month = localDateTime.month
    val weekDay = localDateTime.dayOfWeek

    val year = localDateTime.year

    val currentSeason = when (month) {
        Month.JANUARY, Month.FEBRUARY, Month.DECEMBER -> Season.WINTER
        Month.MARCH, Month.APRIL, Month.MAY -> Season.SPRING
        Month.JUNE, Month.JULY, Month.AUGUST -> Season.SUMMER
        Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Season.FALL
        else -> Season.SPRING
    }

    val currentStartSeason = StartSeason(
        // if december, the season is next year winter
        year = if (month == Month.DECEMBER) year + 1 else year,
        season = currentSeason
    )

    val japanHour = japanDateTime.hour

    val japanWeekDay = japanDateTime.dayOfWeek

    fun DayOfWeek.toWeekDay() = when (this) {
        DayOfWeek.MONDAY -> WeekDay.MONDAY
        DayOfWeek.TUESDAY -> WeekDay.TUESDAY
        DayOfWeek.WEDNESDAY -> WeekDay.WEDNESDAY
        DayOfWeek.THURSDAY -> WeekDay.THURSDAY
        DayOfWeek.FRIDAY -> WeekDay.FRIDAY
        DayOfWeek.SATURDAY -> WeekDay.SATURDAY
        DayOfWeek.SUNDAY -> WeekDay.SUNDAY
        else -> WeekDay.MONDAY
    }
}