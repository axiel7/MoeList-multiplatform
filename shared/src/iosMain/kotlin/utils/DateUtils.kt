package utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSDateFormatter

actual fun LocalDateTime.format(format: String): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = format
    return dateFormatter
        .stringFromDate(this.toNSDateComponents().date
            ?: throw IllegalStateException("Could not convert kotlin date to NSDate $this")
        )
}