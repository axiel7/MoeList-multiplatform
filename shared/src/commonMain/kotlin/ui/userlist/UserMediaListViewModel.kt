package ui.userlist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import animeListSort
import cafe.adriel.voyager.core.model.coroutineScope
import data.datastore.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.getDataStore
import data.model.ApiParams
import data.model.anime.MyAnimeListStatus
import data.model.anime.UserAnimeList
import data.model.manga.MyMangaListStatus
import data.model.manga.UserMangaList
import data.model.media.BaseMediaNode
import data.model.media.BaseMyListStatus
import data.model.media.BaseUserMediaList
import data.model.media.ListType
import data.model.media.MediaSort
import data.model.media.MediaType
import data.repository.AnimeRepository
import data.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mangaListSort
import nsfw
import ui.base.BaseMediaViewModel

class UserMediaListViewModel(
    listType: ListType
) : BaseMediaViewModel() {

    override val mediaType = listType.mediaType

    var listTypeStyle = listType.styleGlobalAppVariable.value

    var listSort by mutableStateOf(
        if (mediaType == MediaType.ANIME) animeListSort else mangaListSort
    )
        private set

    fun setSort(value: MediaSort) {
        coroutineScope.launch(Dispatchers.IO) {
            getDataStore().edit {
                if (mediaType == MediaType.ANIME) it[ANIME_LIST_SORT_PREFERENCE_KEY] = value.value
                else it[MANGA_LIST_SORT_PREFERENCE_KEY] = value.value
            }
            nextPage = null
            hasNextPage = false
            loadedAllPages = false
            listSort = value
        }
    }

    var openSortDialog by mutableStateOf(false)

    private val params = ApiParams(
        status = listType.status.value,
        sort = listSort.value,
        nsfw = nsfw,
        fields = if (mediaType == MediaType.ANIME) AnimeRepository.USER_ANIME_LIST_FIELDS
        else MangaRepository.USER_MANGA_LIST_FIELDS
    )

    var selectedItem by mutableStateOf<BaseUserMediaList<*>?>(null)
    override var myListStatus by object : MutableState<BaseMyListStatus?> {
        override var value: BaseMyListStatus?
            get() = selectedItem?.listStatus
            set(value) {
                when (value) {
                    is MyAnimeListStatus -> selectedItem =
                        (selectedItem as? UserAnimeList)?.copy(listStatus = value)

                    is MyMangaListStatus -> selectedItem =
                        (selectedItem as? UserMangaList)?.copy(listStatus = value)
                }
                onMediaItemStatusChanged(value)
            }

        override fun component1(): BaseMyListStatus? = value
        override fun component2(): (BaseMyListStatus?) -> Unit = { value = it }
    }
    val mediaList = mutableStateListOf<BaseUserMediaList<out BaseMediaNode>>()
    var nextPage: String? = null
    var hasNextPage = false
    var loadedAllPages = false
    var isLoadingList by mutableStateOf(false)

    fun getUserList(page: String? = null) {
        coroutineScope.launch(Dispatchers.IO) {
            isLoading = page == null
            isLoadingList = true
            params.sort = listSort.value
            val result = if (mediaType == MediaType.ANIME)
                AnimeRepository.getUserAnimeList(params, page)
            else
                MangaRepository.getUserMangaList(params, page)

            if (result?.data != null) {
                if (page == null) mediaList.clear()
                mediaList.addAll(result.data)

                nextPage = result.paging?.next
                hasNextPage = nextPage != null
                loadedAllPages = page != null && nextPage == null
            } else {
                setErrorMessage(result?.message ?: result?.error ?: "Generic error")
                hasNextPage = false
            }
            isLoadingList = false
            isLoading = false
        }
    }

    fun updateListItem(
        mediaId: Int,
        status: String? = null,
        score: Int? = null,
        progress: Int? = null,
        volumeProgress: Int? = null,
        startDate: String? = null,
        endDate: String? = null,
        repeatCount: Int? = null,
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            isLoading = true
            val result = if (mediaType == MediaType.ANIME)
                AnimeRepository.updateAnimeEntry(
                    animeId = mediaId,
                    status = status,
                    score = score,
                    watchedEpisodes = progress,
                    startDate = startDate,
                    endDate = endDate,
                    numRewatches = repeatCount
                )
            else MangaRepository.updateMangaEntry(
                mangaId = mediaId,
                status = status,
                score = score,
                chaptersRead = progress,
                volumesRead = volumeProgress,
                startDate = startDate,
                endDate = endDate,
                numRereads = repeatCount
            )

            if (result != null) {
                val foundIndex = mediaList.indexOfFirst { it.node.id == mediaId }
                if (foundIndex != -1) {
                    if (mediaType == MediaType.ANIME) {
                        mediaList[foundIndex] = (mediaList[foundIndex] as UserAnimeList)
                            .copy(listStatus = result as MyAnimeListStatus)
                    } else if (mediaType == MediaType.MANGA) {
                        mediaList[foundIndex] = (mediaList[foundIndex] as UserMangaList)
                            .copy(listStatus = result as MyMangaListStatus)
                    }
                }
            }
            isLoading = false
        }
    }

    fun onItemSelected(item: BaseUserMediaList<*>) {
        selectedItem = item
        mediaInfo = item.node
    }

    private fun onMediaItemStatusChanged(myListStatus: BaseMyListStatus?) {
        coroutineScope.launch(Dispatchers.IO) {
            if (myListStatus != null && selectedItem != null) {
                val foundIndex = mediaList.indexOfFirst { it.node.id == selectedItem!!.node.id }
                if (foundIndex != -1) {
                    if (mediaType == MediaType.ANIME) {
                        mediaList[foundIndex] = (mediaList[foundIndex] as UserAnimeList)
                            .copy(listStatus = myListStatus as MyAnimeListStatus)
                    } else if (mediaType == MediaType.MANGA) {
                        mediaList[foundIndex] = (mediaList[foundIndex] as UserMangaList)
                            .copy(listStatus = myListStatus as MyMangaListStatus)
                    }
                }
            }
        }
    }
}