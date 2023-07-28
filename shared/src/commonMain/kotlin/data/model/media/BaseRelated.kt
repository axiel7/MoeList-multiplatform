package data.model.media

import androidx.compose.runtime.Composable
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource

abstract class BaseRelated {
    abstract val node: BaseMediaNode
    abstract val relationType: String
    abstract val relationTypeFormatted: String
}

@Composable
fun BaseRelated.relationLocalized() = when (this.relationTypeFormatted) {
    "Prequel" -> stringResource(MR.strings.relation_prequel)
    "Sequel" -> stringResource(MR.strings.relation_sequel)
    "Summary" -> stringResource(MR.strings.relation_summary)
    "Alternative version" -> stringResource(MR.strings.relation_alternative_version)
    "Alternative setting" -> stringResource(MR.strings.relation_alternative_setting)
    "Spin-off" -> stringResource(MR.strings.relation_spin_off)
    "Side story" -> stringResource(MR.strings.relation_side_story)
    "Parent story" -> stringResource(MR.strings.parent_story)
    "Full story" -> stringResource(MR.strings.full_story)
    "Adaptation" -> stringResource(MR.strings.adaptation)
    "Other" -> stringResource(MR.strings.relation_other)
    else -> this.relationTypeFormatted
}