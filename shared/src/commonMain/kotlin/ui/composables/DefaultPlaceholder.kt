package ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius

fun Modifier.defaultPlaceholder(
    visible: Boolean
) = composed {
    val color = MaterialTheme.colorScheme.outline
    drawWithContent {
        drawContent()
        if (visible) {
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(x = 8f, y = 8f)
            )
        }
    }
}