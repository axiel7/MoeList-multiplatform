package data.model.anime

import data.model.media.BaseRelated
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelatedAnime(
    @SerialName("node")
    override val node: AnimeNode,
    @SerialName("relation_type")
    override val relationType: String,
    @SerialName("relation_type_formatted")
    override val relationTypeFormatted: String
) : BaseRelated()
