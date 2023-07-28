package ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TextIconHorizontal(
    text: String,
    icon: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.padding(end = 4.dp),
            tint = color
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 4.dp),
            color = color,
            fontSize = fontSize
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TextIconVertical(
    text: String,
    icon: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontSize: TextUnit = TextUnit.Unspecified,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.padding(4.dp),
            tint = color
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .defaultPlaceholder(visible = isLoading),
            color = color,
            fontSize = fontSize
        )
    }
}

@Composable
fun TextIconVertical(
    text: String,
    icon: String,
    modifier: Modifier = Modifier,
    tooltip: String,
    isLoading: Boolean = false,
) {
    var showTooltip by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box {
        if (showTooltip) {
            Popup {
                Text(tooltip)
            }
            scope.launch {
                delay(1000)
                showTooltip = false
            }
        }
        TextIconVertical(
            text = text,
            icon = icon,
            modifier = modifier.clickable {
                showTooltip = true
            },
            isLoading = isLoading
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun TextIconHorizontalPreview() {
    MoeListTheme {
        TextIconHorizontal(text = "This is an example", icon = R.drawable.ic_round_details_star_24)
    }
}

@Preview(showBackground = true)
@Composable
fun TextIconVerticalPreview() {
    MoeListTheme {
        TextIconVertical(text = "This is an example", icon = R.drawable.ic_round_details_star_24)
    }
}
*/
