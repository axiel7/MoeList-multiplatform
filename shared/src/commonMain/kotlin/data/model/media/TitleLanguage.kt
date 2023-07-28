package data.model.media

import com.moelist.common.MR

enum class TitleLanguage {
    ROMAJI, ENGLISH, JAPANESE;

    val stringRes
        get() = when (this) {
            ROMAJI -> MR.strings.romaji
            ENGLISH -> MR.strings.english
            JAPANESE -> MR.strings.japanese
        }
}