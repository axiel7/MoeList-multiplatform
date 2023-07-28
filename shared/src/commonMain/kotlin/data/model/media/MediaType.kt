package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource

enum class MediaType {
    ANIME, MANGA
}

@Composable
fun MediaType.localized() = when (this) {
    MediaType.ANIME -> stringResource(MR.strings.anime)
    MediaType.MANGA -> stringResource(MR.strings.manga)
}