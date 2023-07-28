package data.model.manga

import data.model.media.BaseMediaList
import kotlinx.serialization.Serializable

@Serializable
data class MangaList(
    override val node: MangaNode
) : BaseMediaList()

