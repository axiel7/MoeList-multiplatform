package ui.more

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import openActionUrl
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.composables.AlertDialog
import ui.composables.collapsable
import utils.Constants
import utils.Constants.DISCORD_SERVER_URL
import utils.Constants.GITHUB_ISSUES_URL
import utils.logOut

class MoreScreen(
    private val topBarHeightPx: Float,
    private val topBarOffsetY: Animatable<Float, AnimationVector1D>,
    private val padding: PaddingValues,
) : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        var openFeedbackDialog by remember { mutableStateOf(false) }

        if (openFeedbackDialog) {
            FeedbackDialog(
                onDismiss = { openFeedbackDialog = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .collapsable(
                    state = scrollState,
                    topBarHeightPx = topBarHeightPx,
                    topBarOffsetY = topBarOffsetY,
                )
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            Icon(
                painter = painterResource("ic_moelist_logo"),
                contentDescription = stringResource(MR.strings.app_name),
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Divider()

            MoreItem(
                title = stringResource(MR.strings.anime_manga_news),
                subtitle = stringResource(MR.strings.news_summary),
                icon = "ic_new_releases",
                onClick = { openActionUrl(Constants.MAL_NEWS_URL) }
            )

            MoreItem(
                title = stringResource(MR.strings.mal_announcements),
                subtitle = stringResource(MR.strings.mal_announcements_summary),
                icon = "ic_campaign",
                onClick = { openActionUrl(Constants.MAL_ANNOUNCEMENTS_URL) }
            )

            Divider()

            MoreItem(
                title = stringResource(MR.strings.notifications),
                icon = "round_notifications_24",
                onClick = {
                    TODO("navigateToNotifications")
                }
            )

            MoreItem(
                title = stringResource(MR.strings.settings),
                icon = "ic_round_settings_24",
                onClick = {
                    TODO("navigateToSettings")
                }
            )

            MoreItem(
                title = stringResource(MR.strings.about),
                icon = "ic_info",
                onClick = {
                    TODO("navigateToAbout")
                }
            )

            MoreItem(
                title = stringResource(MR.strings.feedback),
                icon = "ic_round_feedback_24",
                onClick = {
                    openFeedbackDialog = true
                }
            )

            Divider()

            MoreItem(
                title = stringResource(MR.strings.logout),
                subtitle = stringResource(MR.strings.logout_summary),
                icon = "ic_round_power_settings_new_24",
                onClick = {
                    coroutineScope.launch { logOut() }
                }
            )
        }
    }
}

@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(MR.strings.cancel))
            }
        },
        text = {
            Column {
                MoreItem(
                    title = stringResource(MR.strings.github),
                    icon = "ic_github",
                    onClick = {
                        openActionUrl(GITHUB_ISSUES_URL)
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.discord),
                    icon = "ic_discord",
                    onClick = {
                        openActionUrl(DISCORD_SERVER_URL)
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MoreItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                modifier = Modifier.padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Spacer(
                modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp)
            )
        }

        Column(
            modifier = if (subtitle != null)
                Modifier.padding(16.dp)
            else Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun MorePreview() {
    MoeListTheme {
        MoreView(
            navigateToSettings = {},
            navigateToNotifications = {},
            navigateToAbout = {},
            padding = PaddingValues(),
            topBarHeightPx = 0f,
            topBarOffsetY = remember { Animatable(0f) }
        )
    }
}
*/
