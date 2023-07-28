package data.datastore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import androidx.datastore.core.Storage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import animeCompletedListStyle
import animeCurrentListStyle
import animeDroppedListStyle
import animeListSort
import animePausedListStyle
import animePlannedListStyle
import createKtorClient
import data.model.media.MediaSort
import data.model.media.TitleLanguage
import generalListStyle
import gridItemsPerRow
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mangaCompletedListStyle
import mangaCurrentListStyle
import mangaDroppedListStyle
import mangaListSort
import mangaPausedListStyle
import mangaPlannedListStyle
import nsfw
import okio.Path.Companion.toPath
import producePreferencePath
import theme
import titleLanguage
import ui.base.ListStyle
import useGeneralListStyle
import useListTabs
import utils.NumExtensions.toInt
import kotlin.native.concurrent.ThreadLocal

object PreferencesDataStore {

    val ACCESS_TOKEN_PREFERENCE_KEY = stringPreferencesKey("access_token")
    val REFRESH_TOKEN_PREFERENCE_KEY = stringPreferencesKey("refresh_token")
    val NSFW_PREFERENCE_KEY = booleanPreferencesKey("nsfw")
    val LANG_PREFERENCE_KEY = stringPreferencesKey("lang")
    val THEME_PREFERENCE_KEY = stringPreferencesKey("theme")
    val LAST_TAB_PREFERENCE_KEY = intPreferencesKey("last_tab")
    val PROFILE_PICTURE_PREFERENCE_KEY = stringPreferencesKey("profile_picture")
    val ANIME_LIST_SORT_PREFERENCE_KEY = stringPreferencesKey("anime_list_sort")
    val MANGA_LIST_SORT_PREFERENCE_KEY = stringPreferencesKey("manga_list_sort")

    val GENERAL_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("list_display_mode")
    val USE_GENERAL_LIST_STYLE_PREFERENCE_KEY = booleanPreferencesKey("use_general_list_style")
    val GRID_ITEMS_PER_ROW_PREFERENCE_KEY = intPreferencesKey("grid_items_per_row")
    val ANIME_CURRENT_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("anime_current_list_style")
    val ANIME_PLANNED_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("anime_planned_list_style")
    val ANIME_COMPLETED_LIST_STYLE_PREFERENCE_KEY =
        stringPreferencesKey("anime_completed_list_style")
    val ANIME_PAUSED_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("anime_paused_list_style")
    val ANIME_DROPPED_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("anime_dropped_list_style")
    val MANGA_CURRENT_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("manga_current_list_style")
    val MANGA_PLANNED_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("manga_planned_list_style")
    val MANGA_COMPLETED_LIST_STYLE_PREFERENCE_KEY =
        stringPreferencesKey("manga_completed_list_style")
    val MANGA_PAUSED_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("manga_paused_list_style")
    val MANGA_DROPPED_LIST_STYLE_PREFERENCE_KEY = stringPreferencesKey("manga_dropped_list_style")

    val START_TAB_PREFERENCE_KEY = stringPreferencesKey("start_tab")
    val TITLE_LANG_PREFERENCE_KEY = stringPreferencesKey("title_lang")
    val USE_LIST_TABS_PREFERENCE_KEY = booleanPreferencesKey("use_list_tabs")


    private lateinit var defaultPreferencesDataStore: DataStore<Preferences>

    private val lock = SynchronizedObject()

    /**
     * Gets the singleton DataStore instance, creating it if necessary.
     */
    fun getDataStore(): DataStore<Preferences> =
        synchronized(lock) {
            if (::defaultPreferencesDataStore.isInitialized) {
                defaultPreferencesDataStore
            } else {
                PreferenceDataStoreFactory.createWithPath(
                    produceFile = { producePreferencePath().toPath() }
                ).also { defaultPreferencesDataStore = it }
            }
        }

    internal const val dataStoreFileName = "moelist.preferences"

    //val Context.notificationsDataStore by preferencesDataStore(name = "notifications")

    /**
     * Gets the value by blocking the main thread
     */
    fun <T> DataStore<Preferences>.getValueSync(
        key: Preferences.Key<T>
    ) = runBlocking { data.first() }[key]

    @Composable
    fun <T> rememberPreference(
        key: Preferences.Key<T>,
        defaultValue: T,
    ): MutableState<T> {
        val coroutineScope = rememberCoroutineScope()
        val state = remember {
            getDataStore().data
                .map {
                    it[key] ?: defaultValue
                }
        }.collectAsState(initial = defaultValue)

        return remember {
            object : MutableState<T> {
                override var value: T
                    get() = state.value
                    set(value) {
                        coroutineScope.launch {
                            getDataStore().edit {
                                it[key] = value
                            }
                        }
                    }

                override fun component1() = value
                override fun component2(): (T) -> Unit = { value = it }
            }
        }
    }

    fun preloadPreferences() {
        getDataStore().getValueSync(THEME_PREFERENCE_KEY)?.let {
            theme = it
        }
        getDataStore().getValueSync(ACCESS_TOKEN_PREFERENCE_KEY)?.let {
            createKtorClient(accessToken = it)
        }
        getDataStore().getValueSync(NSFW_PREFERENCE_KEY)?.let {
            nsfw = it.toInt()
        }
        getDataStore().getValueSync(ANIME_LIST_SORT_PREFERENCE_KEY)?.let {
            MediaSort.forValue(it)?.let { sort -> animeListSort = sort }
        }
        getDataStore().getValueSync(MANGA_LIST_SORT_PREFERENCE_KEY)?.let {
            MediaSort.forValue(it)?.let { sort -> mangaListSort = sort }
        }

        getDataStore().getValueSync(GENERAL_LIST_STYLE_PREFERENCE_KEY)?.let {
            ListStyle.forValue(it)?.let { mode -> generalListStyle = mode }
        }
        getDataStore().getValueSync(USE_GENERAL_LIST_STYLE_PREFERENCE_KEY)?.let {
            useGeneralListStyle = it
        }
        getDataStore().getValueSync(ANIME_CURRENT_LIST_STYLE_PREFERENCE_KEY)?.let {
            ListStyle.forValue(it)?.let { mode -> animeCurrentListStyle = mode }
        }
        getDataStore().getValueSync(MANGA_CURRENT_LIST_STYLE_PREFERENCE_KEY)?.let {
            ListStyle.forValue(it)?.let { mode -> mangaCurrentListStyle = mode }
        }

        getDataStore().getValueSync(TITLE_LANG_PREFERENCE_KEY)?.let {
            titleLanguage = TitleLanguage.valueOf(it)
        }
        getDataStore().getValueSync(USE_LIST_TABS_PREFERENCE_KEY)?.let {
            useListTabs = it
        }
        getDataStore().getValueSync(GRID_ITEMS_PER_ROW_PREFERENCE_KEY)?.let {
            gridItemsPerRow = it
        }

        // load preferences used later in another thread
        CoroutineScope(Dispatchers.IO).launch {
            getDataStore().getValueSync(ANIME_PLANNED_LIST_STYLE_PREFERENCE_KEY)?.let {
                ListStyle.forValue(it)?.let { mode -> animePlannedListStyle = mode }
            }
            getDataStore().getValueSync(ANIME_COMPLETED_LIST_STYLE_PREFERENCE_KEY)
                ?.let {
                    ListStyle.forValue(it)?.let { mode -> animeCompletedListStyle = mode }
                }
            getDataStore().getValueSync(ANIME_PAUSED_LIST_STYLE_PREFERENCE_KEY)?.let {
                ListStyle.forValue(it)?.let { mode -> animePausedListStyle = mode }
            }
            getDataStore().getValueSync(ANIME_DROPPED_LIST_STYLE_PREFERENCE_KEY)?.let {
                ListStyle.forValue(it)?.let { mode -> animeDroppedListStyle = mode }
            }
            getDataStore().getValueSync(MANGA_PLANNED_LIST_STYLE_PREFERENCE_KEY)?.let {
                ListStyle.forValue(it)?.let { mode -> mangaPlannedListStyle = mode }
            }
            getDataStore().getValueSync(MANGA_COMPLETED_LIST_STYLE_PREFERENCE_KEY)
                ?.let {
                    ListStyle.forValue(it)?.let { mode -> mangaCompletedListStyle = mode }
                }
            getDataStore().getValueSync(MANGA_PAUSED_LIST_STYLE_PREFERENCE_KEY)?.let {
                ListStyle.forValue(it)?.let { mode -> mangaPausedListStyle = mode }
            }
            getDataStore().getValueSync(MANGA_DROPPED_LIST_STYLE_PREFERENCE_KEY)?.let {
                ListStyle.forValue(it)?.let { mode -> mangaDroppedListStyle = mode }
            }
        }
    }
}