package ui.base

import com.moelist.common.MR

enum class ItemsPerRow(val value: Int) {
    DEFAULT(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);

    val stringRes
        get() = when (this) {
            DEFAULT -> MR.strings.default_setting
            ONE -> MR.strings.one
            TWO -> MR.strings.two
            THREE -> MR.strings.three
            FOUR -> MR.strings.four
            FIVE -> MR.strings.five
            SIX -> MR.strings.six
            SEVEN -> MR.strings.seven
            EIGHT -> MR.strings.eight
            NINE -> MR.strings.nine
            TEN -> MR.strings.ten
        }
}