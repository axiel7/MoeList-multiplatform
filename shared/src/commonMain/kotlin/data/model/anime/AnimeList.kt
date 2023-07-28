package data.model.anime

import data.model.media.BaseMediaList
import kotlinx.serialization.Serializable

@Serializable
data class AnimeList(
    override val node: AnimeNode
) : BaseMediaList()
