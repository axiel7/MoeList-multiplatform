package data.model.media

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainPicture(
    @SerialName("medium")
    val medium: String,
    @SerialName("large")
    val large: String
)