package utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

actual fun LocalDateTime.format(
    format: String
): String = DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDateTime())