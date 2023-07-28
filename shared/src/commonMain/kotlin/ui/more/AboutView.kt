package ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import getAppVersion
import openActionUrl
import showToast
import ui.composables.DefaultScaffoldWithTopAppBar
import utils.Constants.DISCORD_SERVER_URL
import utils.Constants.GITHUB_REPO_URL

class AboutScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var versionClicks by remember { mutableStateOf(0) }

        DefaultScaffoldWithTopAppBar(
            title = stringResource(MR.strings.about),
            navigateBack = { navigator.pop() }
        ) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                MoreItem(
                    title = stringResource(MR.strings.version),
                    subtitle = getAppVersion(),
                    icon = "ic_moelist_logo",
                    onClick = {
                        if (versionClicks >= 7) {
                            showToast("✧◝(⁰▿⁰)◜✧")
                            versionClicks = 0
                        } else versionClicks++
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.discord),
                    subtitle = stringResource(MR.strings.discord_summary),
                    icon = "ic_discord",
                    onClick = {
                        openActionUrl(DISCORD_SERVER_URL)
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.github),
                    subtitle = stringResource(MR.strings.github_summary),
                    icon = "ic_github",
                    onClick = {
                        openActionUrl(GITHUB_REPO_URL)
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.credits),
                    subtitle = stringResource(MR.strings.credits_summary),
                    icon = "ic_round_group_24",
                    onClick = {
                        TODO("navigateToCredits")
                    }
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun AboutPreview() {
    MoeListTheme {
        AboutView(
            navigateBack = {},
            navigateToCredits = {}
        )
    }
}
*/
