package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import data.model.BaseResponse
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.SerialName

abstract class BaseMyListStatus : BaseResponse() {
    @SerialName("status")
    abstract val status: ListStatus

    @SerialName("score")
    abstract val score: Int

    @SerialName("updated_at")
    abstract val updatedAt: String?

    @SerialName("start_date")
    abstract val startDate: String?

    @SerialName("end_date")
    abstract val endDate: String?

    abstract val progress: Int?
    abstract val repeatCount: Int?
    abstract val isRepeating: Boolean

    override val error: String? = null
    override val message: String? = null
}

@Composable
fun Int.scoreText() = when (this) {
    0 -> "─"
    1 -> stringResource(MR.strings.score_apalling)
    2 -> stringResource(MR.strings.score_horrible)
    3 -> stringResource(MR.strings.score_very_bad)
    4 -> stringResource(MR.strings.score_bad)
    5 -> stringResource(MR.strings.score_average)
    6 -> stringResource(MR.strings.score_fine)
    7 -> stringResource(MR.strings.score_good)
    8 -> stringResource(MR.strings.score_very_good)
    9 -> stringResource(MR.strings.score_great)
    10 -> stringResource(MR.strings.score_masterpiece)
    else -> "─"
}