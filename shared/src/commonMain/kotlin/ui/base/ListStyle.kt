package ui.base

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource

enum class ListStyle(val value: String) {
    STANDARD("standard"),
    COMPACT("compact"),
    MINIMAL("minimal"),
    GRID("grid");

    override fun toString(): String {
        return value
    }

    companion object {
        fun forValue(value: String) = ListStyle.values().firstOrNull { it.value == value }
    }
}

val ListStyle.stringRes
    get() = when (this) {
        ListStyle.STANDARD -> MR.strings.list_mode_standard
        ListStyle.COMPACT -> MR.strings.list_mode_compact
        ListStyle.MINIMAL -> MR.strings.list_mode_minimal
        ListStyle.GRID -> MR.strings.list_mode_grid
    }

@Composable
fun ListStyle.localized() = stringResource(stringRes)