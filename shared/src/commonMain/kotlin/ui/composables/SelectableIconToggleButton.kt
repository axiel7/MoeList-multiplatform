package ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun <T> SelectableIconToggleButton(
    icon: String,
    tooltipText: String,
    value: T,
    selectedValue: T,
    onClick: (Boolean) -> Unit
) {
    var showTooltip by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box {
        if (showTooltip) {
            Popup {
                Text(tooltipText)
            }
            scope.launch {
                delay(1000)
                showTooltip = false
            }
        }
        FilledIconToggleButton(
            checked = value == selectedValue,
            onCheckedChange = {
                showTooltip = true
                onClick(it)
            },
            //modifier = Modifier.tooltipTrigger()
        ) {
            Icon(painter = painterResource(icon), contentDescription = tooltipText)
        }
    }
}