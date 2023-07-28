package ui.more

import Platform
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import changeLanguage
import com.moelist.common.MR
import data.datastore.PreferencesDataStore.GENERAL_LIST_STYLE_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.GRID_ITEMS_PER_ROW_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.LANG_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.NSFW_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.START_TAB_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.THEME_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.TITLE_LANG_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.USE_GENERAL_LIST_STYLE_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.USE_LIST_TABS_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.rememberPreference
import data.model.media.TitleLanguage
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import generalListStyle
import getWindowSize
import gridItemsPerRow
import nsfw
import openByDefaultSettings
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import showToast
import titleLanguage
import ui.base.AppLanguage
import ui.base.ItemsPerRow
import ui.base.ListStyle
import ui.base.ThemeStyle
import ui.base.stringRes
import ui.composables.AlertDialog
import ui.composables.DefaultScaffoldWithTopAppBar
import useGeneralListStyle
import useListTabs
import utils.NumExtensions.toInt

val themeEntries = ThemeStyle.values().associate { it.name.lowercase() to it.stringRes }
val languageEntries = AppLanguage.values().associate { it.value to it.stringResNative }
val listStyleEntries = ListStyle.values().associate { it.value to it.stringRes }
val itemsPerRowEntries = ItemsPerRow.values().associate { it.value.toString() to it.stringRes }
val titleLanguageEntries = TitleLanguage.values().associate { it.name to it.stringRes }
val startTabEntries = mapOf(
    "last_used" to MR.strings.last_used,
    "home" to MR.strings.title_home,
    "anime_list" to MR.strings.title_anime_list,
    "manga_list" to MR.strings.title_manga_list,
    "more" to MR.strings.more
)

class SettingsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var langPreference by rememberPreference(
            LANG_PREFERENCE_KEY,
            AppLanguage.FOLLOW_SYSTEM.value
        )
        var themePreference by rememberPreference(
            THEME_PREFERENCE_KEY,
            ThemeStyle.FOLLOW_SYSTEM.name.lowercase()
        )
        var nsfwPreference by rememberPreference(NSFW_PREFERENCE_KEY, false)
        var useGeneralListStyle by rememberPreference(
            USE_GENERAL_LIST_STYLE_PREFERENCE_KEY,
            useGeneralListStyle
        )
        var generalListStylePreference by rememberPreference(
            GENERAL_LIST_STYLE_PREFERENCE_KEY,
            ListStyle.STANDARD.value
        )
        var itemsPerRowPreference by rememberPreference(
            GRID_ITEMS_PER_ROW_PREFERENCE_KEY,
            gridItemsPerRow
        )
        var startTabPreference by rememberPreference(START_TAB_PREFERENCE_KEY, "last_used")
        var titleLangPreference by rememberPreference(
            TITLE_LANG_PREFERENCE_KEY,
            titleLanguage.name
        )
        var useListTabsPreference by rememberPreference(USE_LIST_TABS_PREFERENCE_KEY, useListTabs)

        DefaultScaffoldWithTopAppBar(
            title = stringResource(MR.strings.settings),
            navigateBack = { navigator.pop() }
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {
                SettingsTitle(text = stringResource(MR.strings.display))

                ListPreferenceView(
                    title = stringResource(MR.strings.theme),
                    entriesValues = themeEntries,
                    value = themePreference,
                    icon = "ic_round_color_lens_24",
                    onValueChange = { value ->
                        themePreference = value
                    }
                )

                ListPreferenceView(
                    title = stringResource(MR.strings.language),
                    entriesValues = languageEntries,
                    value = langPreference,
                    icon = "ic_round_language_24",
                    onValueChange = { value ->
                        langPreference = value
                        changeLanguage(value)
                        if (value == "ja") {
                            TitleLanguage.JAPANESE.let {
                                titleLangPreference = it.name
                                titleLanguage = it
                            }
                        }
                    }
                )

                ListPreferenceView(
                    title = stringResource(MR.strings.title_language),
                    entriesValues = titleLanguageEntries,
                    value = titleLangPreference,
                    icon = "round_title_24",
                    onValueChange = { value ->
                        titleLangPreference = value
                        titleLanguage = TitleLanguage.valueOf(value)
                    }
                )

                ListPreferenceView(
                    title = stringResource(MR.strings.default_section),
                    entriesValues = startTabEntries,
                    value = startTabPreference,
                    icon = "ic_round_home_24",
                    onValueChange = { value ->
                        startTabPreference = value
                    }
                )

                SwitchPreferenceView(
                    title = stringResource(MR.strings.use_separated_list_styles),
                    value = !useGeneralListStyle,
                    onValueChange = {
                        useGeneralListStyle = !it
                    }
                )

                if (useGeneralListStyle) {
                    ListPreferenceView(
                        title = stringResource(MR.strings.list_style),
                        entriesValues = listStyleEntries,
                        value = generalListStylePreference,
                        icon = "round_format_list_bulleted_24",
                        onValueChange = { value ->
                            ListStyle.forValue(value)?.let {
                                generalListStylePreference = it.value
                                generalListStyle = it
                            }
                        }
                    )
                } else {
                    PlainPreferenceView(
                        title = stringResource(MR.strings.list_style),
                        icon = "round_format_list_bulleted_24",
                        onClick = {
                            navigator.push(ListStyleSettingsScreen())
                        }
                    )
                }

                if (generalListStylePreference == ListStyle.GRID.value || !useGeneralListStyle) {
                    ListPreferenceView(
                        title = stringResource(MR.strings.items_per_row),
                        entriesValues = itemsPerRowEntries,
                        value = itemsPerRowPreference.toString(),
                        icon = "round_grid_view_24",
                        onValueChange = { value ->
                            value.toIntOrNull()?.let {
                                itemsPerRowPreference = it
                                gridItemsPerRow = it
                            }
                        }
                    )
                }

                SettingsTitle(text = stringResource(MR.strings.content))

                SwitchPreferenceView(
                    title = stringResource(MR.strings.show_nsfw),
                    subtitle = stringResource(MR.strings.nsfw_summary),
                    value = nsfwPreference,
                    icon = "no_adult_content_24",
                    onValueChange = { value ->
                        nsfwPreference = value
                        nsfw = value.toInt()
                    }
                )

                Platform.androidSdkVersion?.let { sdkInt ->
                    if (sdkInt >= 33) {
                        PlainPreferenceView(
                            title = stringResource(MR.strings.open_mal_links_in_the_app),
                            icon = "ic_open_in_browser",
                            onClick = {
                                openByDefaultSettings()
                            }
                        )
                    }
                }

                SettingsTitle(text = stringResource(MR.strings.experimental))

                SwitchPreferenceView(
                    title = "Enable list tabs",
                    subtitle = "Use tabs in Anime/Manga list instead of Floating Action Button",
                    value = useListTabsPreference,
                    onValueChange = {
                        useListTabsPreference = it
                        showToast(
                            //context.getString(MR.strings.changes_will_take_effect_on_app_restart)
                            "changes_will_take_effect_on_app_restart"
                        )
                    }
                )
            }//:Column
        }//:Scaffold
    }
}

@Composable
fun SettingsTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 72.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PlainPreferenceView(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
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
            }//: Column
        }//: Row
    }//: Row
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SwitchPreferenceView(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: Boolean,
    icon: String? = null,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onValueChange(!value)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
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
                        fontSize = 13.sp,
                        lineHeight = 14.sp
                    )
                }
            }//: Column
        }//: Row

        Switch(
            checked = value,
            onCheckedChange = {
                onValueChange(it)
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }//: Row
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ListPreferenceView(
    title: String,
    entriesValues: Map<String, StringResource>,
    modifier: Modifier = Modifier,
    value: String,
    icon: String? = null,
    onValueChange: (String) -> Unit
) {
    //val configuration = LocalConfiguration.current
    var openDialog by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { openDialog = true },
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(entriesValues[value]!!),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text(text = title) },
            text = {
                LazyColumn(
                    modifier = Modifier.sizeIn(
                        maxHeight = getWindowSize().height - 48.dp
                    )
                ) {
                    items(entriesValues.entries.toList()) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onValueChange(entry.key) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = value == entry.key,
                                onClick = { onValueChange(entry.key) }
                            )
                            Text(text = stringResource(entry.value))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        onValueChange(value)
                    }
                ) {
                    Text(text = stringResource(MR.strings.ok))
                }
            }
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MoeListTheme {
        SettingsView(
            navigateToListStyleSettings = {},
            navigateBack = {}
        )
    }
}
*/