package ui.search

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.coroutineScope
import data.model.ApiParams
import data.model.media.BaseMediaList
import data.model.media.MediaType
import data.repository.AnimeRepository
import data.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import nsfw
import ui.base.BaseViewModel

class SearchViewModel : BaseViewModel() {

    private val params = ApiParams(
        nsfw = nsfw,
        fields = AnimeRepository.SEARCH_FIELDS
    )
    var nextPage: String? = null
    var hasNextPage = false

    val mediaList = mutableStateListOf<BaseMediaList>()

    fun search(
        mediaType: MediaType,
        query: String,
        page: String? = null
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            if (page == null) {
                mediaList.clear()
                isLoading = true
            }
            params.q = query
            val result = if (mediaType == MediaType.ANIME)
                AnimeRepository.searchAnime(params, page)
            else
                MangaRepository.searchManga(params, page)

            if (result?.data != null) {
                mediaList.addAll(result.data)

                nextPage = result.paging?.next
                hasNextPage = nextPage != null
            } else {
                setErrorMessage(result?.message ?: "Generic error")
                hasNextPage = false
            }
            isLoading = false
        }
    }
}