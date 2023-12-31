package ui.ranking

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.coroutineScope
import data.model.ApiParams
import data.model.media.BaseRanking
import data.model.media.MediaType
import data.model.media.RankingType
import data.repository.AnimeRepository
import data.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import nsfw
import ui.base.BaseViewModel

class MediaRankingViewModel(
    private val mediaType: MediaType,
    private val rankingType: RankingType
) : BaseViewModel() {

    private val params = ApiParams(
        nsfw = nsfw,
        fields = AnimeRepository.RANKING_FIELDS
    )

    val mediaList = mutableStateListOf<BaseRanking>()
    var nextPage: String? = null
    var hasNextPage = false
    var loadedAllPages = false

    fun getRanking(page: String? = null) {
        coroutineScope.launch(Dispatchers.IO) {
            isLoading = page == null //show indicator on 1st load
            val result = if (mediaType == MediaType.ANIME)
                AnimeRepository.getAnimeRanking(rankingType, params, page)
            else
                MangaRepository.getMangaRanking(rankingType, params, page)

            if (result?.data != null) {
                if (page == null) mediaList.clear()
                mediaList.addAll(result.data)

                nextPage = result.paging?.next
                hasNextPage = nextPage != null
                loadedAllPages = page != null && nextPage == null
            } else {
                setErrorMessage(result?.message ?: "Generic error")
                hasNextPage = false
            }
            isLoading = false
        }
    }
}