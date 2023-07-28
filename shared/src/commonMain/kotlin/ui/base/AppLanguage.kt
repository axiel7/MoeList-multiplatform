package ui.base

import com.moelist.common.MR

enum class AppLanguage(val value: String) {
    FOLLOW_SYSTEM("follow_system"),
    ENGLISH("en"),
    ARABIC("ar-rSA"),
    BULGARIAN("bg-rBG"),
    CHINESE_SIMPLIFIED("zh-Hans"),
    CHINESE_TRADITIONAL("zh-Hant"),
    CZECH("cs-rCZ"),
    FRENCH("fr"),
    GERMAN("de"),
    INDONESIAN("in-rID"),
    JAPANESE("ja"),
    PORTUGUESE("pt-rPT"),
    PORTUGUESE_BRAZILIAN("pt-rBR"),
    RUSSIAN("ru-rRU"),
    SPANISH("es"),
    TURKISH("tr"),
    UKRAINIAN("uk-rUA");

    val stringResNative
        get() = when (this) {
            FOLLOW_SYSTEM -> MR.strings.theme_system
            ENGLISH -> MR.strings.english_native
            ARABIC -> MR.strings.arabic_native
            BULGARIAN -> MR.strings.bulgarian_native
            CHINESE_SIMPLIFIED -> MR.strings.chinese_simplified_native
            CHINESE_TRADITIONAL -> MR.strings.chinese_traditional_native
            CZECH -> MR.strings.czech_native
            FRENCH -> MR.strings.french_native
            GERMAN -> MR.strings.german_native
            INDONESIAN -> MR.strings.indonesian_native
            JAPANESE -> MR.strings.japanese_native
            PORTUGUESE -> MR.strings.portuguese_native
            PORTUGUESE_BRAZILIAN -> MR.strings.brazilian_native
            RUSSIAN -> MR.strings.russian_native
            SPANISH -> MR.strings.spanish_native
            TURKISH -> MR.strings.turkish_native
            UKRAINIAN -> MR.strings.ukrainian_native
        }
}