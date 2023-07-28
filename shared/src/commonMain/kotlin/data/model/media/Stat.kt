package data.model.media

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.StringResource

data class Stat(
    val title: StringResource,
    val value: Float,
    val color: Color,
)
