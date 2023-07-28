package data.model.anime

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Season(val value: String) {
    @SerialName("winter")
    WINTER("winter"),

    @SerialName("spring")
    SPRING("spring"),

    @SerialName("summer")
    SUMMER("summer"),

    @SerialName("fall")
    FALL("fall")
}

@Composable
fun Season.localized() = when (this) {
    Season.WINTER -> stringResource(MR.strings.winter)
    Season.SPRING -> stringResource(MR.strings.spring)
    Season.SUMMER -> stringResource(MR.strings.summer)
    Season.FALL -> stringResource(MR.strings.fall)
}

fun Season.icon() = when (this) {
    Season.WINTER -> "ic_winter_24"
    Season.SPRING -> "ic_spring_24"
    Season.SUMMER -> "ic_summer_24"
    Season.FALL -> "ic_fall_24"
}