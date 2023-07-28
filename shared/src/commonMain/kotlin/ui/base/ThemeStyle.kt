package ui.base

import com.moelist.common.MR

enum class ThemeStyle {
    FOLLOW_SYSTEM, LIGHT, DARK, BLACK;

    val stringRes
        get() = when (this) {
            FOLLOW_SYSTEM -> MR.strings.theme_system
            LIGHT -> MR.strings.theme_light
            DARK -> MR.strings.theme_dark
            BLACK -> MR.strings.theme_black
        }
}