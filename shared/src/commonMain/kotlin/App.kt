import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import data.datastore.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.LAST_TAB_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.THEME_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.getDataStore
import data.datastore.PreferencesDataStore.getValueSync
import data.datastore.PreferencesDataStore.preloadPreferences
import data.datastore.PreferencesDataStore.rememberPreference
import data.model.media.MediaSort
import data.model.media.TitleLanguage
import data.network.Api
import data.network.JikanApi
import data.network.KtorClient
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.MainView
import ui.base.ListStyle
import ui.theme.MoeListTheme

@Composable
fun MainApp() {
    val theme by rememberPreference(THEME_PREFERENCE_KEY, theme)
    val lastTabOpened by rememberPreference(LAST_TAB_PREFERENCE_KEY, 0)

    MoeListTheme(
        darkTheme = if (theme == "follow_system") isSystemInDarkTheme()
        else theme == "dark" || theme == "black",
        amoledColors = theme == "black"
    ) {
        Surface {
            MainView(lastTabOpened = lastTabOpened)
        }
    }
}

expect fun getAppVersion(): String

expect fun producePreferencePath(): String

@Composable
expect fun getWindowSize(): DpSize

@Composable
expect fun getColorScheme(
    darkTheme: Boolean,
    amoledColors: Boolean,
): ColorScheme

expect fun showToast(message: String)

expect fun openLoginUrl(loginUrl: String, useExternalBrowser: Boolean)

expect fun openActionUrl(url: String)

expect fun changeLanguage(locale: String)

expect fun openByDefaultSettings()

lateinit var api: Api
lateinit var jikanApi: JikanApi

// Global preferences
var theme = "follow_system"
var nsfw = 0
var animeListSort = MediaSort.ANIME_TITLE
var mangaListSort = MediaSort.MANGA_TITLE

var generalListStyle = ListStyle.STANDARD
var useGeneralListStyle = true
var gridItemsPerRow = 0
var animeCurrentListStyle = ListStyle.STANDARD
var animePlannedListStyle = ListStyle.STANDARD
var animeCompletedListStyle = ListStyle.STANDARD
var animePausedListStyle = ListStyle.STANDARD
var animeDroppedListStyle = ListStyle.STANDARD
var mangaCurrentListStyle = ListStyle.STANDARD
var mangaPlannedListStyle = ListStyle.STANDARD
var mangaCompletedListStyle = ListStyle.STANDARD
var mangaPausedListStyle = ListStyle.STANDARD
var mangaDroppedListStyle = ListStyle.STANDARD

var titleLanguage = TitleLanguage.ROMAJI
var useListTabs = false

fun createKtorClient(accessToken: String?) {
    val client = KtorClient(accessToken).ktorHttpClient
    api = Api(client)
    jikanApi = JikanApi(client)
}

fun onCreateApp() {
    val token = getDataStore().getValueSync(ACCESS_TOKEN_PREFERENCE_KEY)
    createKtorClient(token)
    preloadPreferences()
}