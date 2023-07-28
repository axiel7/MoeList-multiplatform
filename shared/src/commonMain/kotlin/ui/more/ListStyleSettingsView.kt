package ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import data.datastore.PreferencesDataStore.rememberPreference
import data.model.media.ListType
import data.model.media.MediaType
import data.model.media.icon
import data.model.media.listStatusAnimeValues
import data.model.media.listStatusMangaValues
import data.model.media.localized
import dev.icerock.moko.resources.compose.stringResource
import ui.composables.DefaultScaffoldWithTopAppBar

class ListStyleSettingsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        DefaultScaffoldWithTopAppBar(
            title = stringResource(MR.strings.list_style),
            navigateBack = {
                navigator.pop()
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {
                Text(
                    text = stringResource(MR.strings.changes_will_take_effect_on_app_restart),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SettingsTitle(text = stringResource(MR.strings.title_anime_list))
                listStatusAnimeValues.forEach { status ->
                    val listType = ListType(status, MediaType.ANIME)
                    var preference by rememberPreference(
                        listType.stylePreferenceKey,
                        listType.styleGlobalAppVariable.value
                    )

                    ListPreferenceView(
                        title = status.localized(),
                        entriesValues = listStyleEntries,
                        value = preference,
                        icon = status.icon(),
                        onValueChange = {
                            preference = it
                        }
                    )
                }

                SettingsTitle(text = stringResource(MR.strings.title_manga_list))
                listStatusMangaValues.forEach { status ->
                    val listType = ListType(status, MediaType.MANGA)
                    var preference by rememberPreference(
                        listType.stylePreferenceKey,
                        listType.styleGlobalAppVariable.value
                    )

                    ListPreferenceView(
                        title = status.localized(),
                        entriesValues = listStyleEntries,
                        value = preference,
                        icon = status.icon(),
                        onValueChange = {
                            preference = it
                        }
                    )
                }
            }
        }
    }
}

/*
@Preview
@Composable
fun ListStyleSettingsViewPreview() {
    ListStyleSettingsView(
        navigateBack = {}
    )
}
*/