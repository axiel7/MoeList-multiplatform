package data.model.manga

import data.model.media.BaseUserMediaList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMangaList(
    @SerialName("node")
    override val node: MangaNode,
    @SerialName("list_status")
    override val listStatus: MyMangaListStatus? = null,
    @SerialName("status")
    override val status: String? = null
) : BaseUserMediaList<MangaNode>()