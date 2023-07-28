package ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BackIconButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(painter = painterResource("ic_arrow_back"), contentDescription = "arrow_back")
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ViewInBrowserButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource("ic_open_in_browser"),
            contentDescription = stringResource(MR.strings.view_on_mal)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ShareButton(
    url: String
) {
    IconButton(onClick = { TODO() }) {
        Icon(
            painter = painterResource("round_share_24"),
            contentDescription = stringResource(MR.strings.share)
        )
    }
}