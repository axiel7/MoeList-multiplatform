package ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import openActionUrl
import ui.composables.DefaultScaffoldWithTopAppBar
import utils.Constants.GENERAL_HELP_CREDIT_URL
import utils.Constants.LOGO_CREDIT_URL

val translationsCredits = mapOf(
    MR.strings.ukrainian to "@Sensetivity",
    MR.strings.turkish to "@hsinankirdar",
    MR.strings.brazilian to "@RickyM7, @SamOak",
    MR.strings.russian to "@grin3671",
    MR.strings.arabic to "@sakugaky, @WhiteCanvas, @Comikazie",
    MR.strings.german to "@Secresa, @MaximilianGT500",
    MR.strings.bulgarian to "@itzlighter",
    MR.strings.czech to "@J4kub07",
    MR.strings.french to "@mamanamgae, @frosqh",
    MR.strings.indonesian to "@Clxf12",
    MR.strings.chinese_traditional to "@jhih_yu_lin",
    MR.strings.chinese_simplified to "@bengerlorf",
    MR.strings.japanese to "@axiel7, @Ulong32, @watashibeme",
    MR.strings.spanish to "@axiel7",
)

class CreditsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()

        DefaultScaffoldWithTopAppBar(
            title = stringResource(MR.strings.credits),
            navigateBack = {
                navigator.pop()
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(it)
            ) {
                Divider()
                SettingsTitle(text = stringResource(MR.strings.support))
                MoreItem(
                    title = stringResource(MR.strings.logo_design),
                    subtitle = "@danielvd_art",
                    onClick = {
                        openActionUrl(LOGO_CREDIT_URL)
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.new_logo_design),
                    subtitle = "@WSTxda",
                    onClick = {
                        openActionUrl("https://www.instagram.com/wstxda/")
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.website),
                    subtitle = "@MaximilianGT500",
                    onClick = {
                        openActionUrl("https://github.com/MaximilianGT500")
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.general_help),
                    subtitle = "@Jeluchu",
                    onClick = {
                        openActionUrl(GENERAL_HELP_CREDIT_URL)
                    }
                )
                MoreItem(
                    title = stringResource(MR.strings.api_help),
                    subtitle = "@Glodanif",
                    onClick = {
                        openActionUrl(LOGO_CREDIT_URL)
                    }
                )
                Divider()
                SettingsTitle(text = stringResource(MR.strings.translations))
                translationsCredits.forEach { (stringRes, credit) ->
                    MoreItem(
                        title = stringResource(stringRes),
                        subtitle = credit,
                        onClick = { }
                    )
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun CreditsPreview() {
    MoeListTheme {
        CreditsView(
            navigateBack = {}
        )
    }
}
*/