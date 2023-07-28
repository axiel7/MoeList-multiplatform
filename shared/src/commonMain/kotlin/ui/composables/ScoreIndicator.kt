package ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moelist.common.MR
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import utils.NumExtensions.toStringPositiveValueOrUnknown

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SmallScoreIndicator(
    score: Float?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource("ic_round_star_16"),
            contentDescription = stringResource(MR.strings.mean_score),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = score.toStringPositiveValueOrUnknown(),
            modifier = Modifier.padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outline,
            fontSize = fontSize
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun SmallScoreIndicatorPreview() {
    MoeListTheme {
        SmallScoreIndicator(score = 8.53f)
    }
}
*/
