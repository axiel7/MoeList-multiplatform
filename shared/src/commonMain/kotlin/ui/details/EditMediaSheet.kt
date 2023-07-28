package ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.axiel7.moelist.uicompose.details.EditMediaViewModel
import com.moelist.common.MR
import com.moriatsushi.insetsx.ExperimentalSoftwareKeyboardApi
import com.moriatsushi.insetsx.imePadding
import data.model.manga.MangaNode
import data.model.media.MediaType
import data.model.media.icon
import data.model.media.listStatusAnimeValues
import data.model.media.listStatusMangaValues
import data.model.media.localized
import data.model.media.scoreText
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import showToast
import ui.base.BaseMediaViewModel
import ui.composables.AlertDialog
import ui.composables.ClickableOutlinedTextField
import ui.composables.SelectableIconToggleButton
import utils.DateUtils
import utils.StringExtensions.toStringOrEmpty

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSoftwareKeyboardApi::class,
    ExperimentalResourceApi::class
)
@Composable
fun Screen.EditMediaSheet(
    mediaViewModel: BaseMediaViewModel,
    onDismiss: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val statusValues =
        if (mediaViewModel.mediaType == MediaType.ANIME) listStatusAnimeValues else listStatusMangaValues
    val datePickerState = rememberDatePickerState()
    val viewModel = rememberScreenModel {
        EditMediaViewModel(
            mediaType = mediaViewModel.mediaType,
            mediaInfo = mediaViewModel.mediaInfo
        )
    }
    val isNewEntry by remember {
        derivedStateOf { mediaViewModel.myListStatus == null }
    }

    if (viewModel.openDatePicker) {
        EditMediaDatePicker(
            viewModel = viewModel,
            datePickerState = datePickerState,
            onDateSelected = {
                when (viewModel.selectedDateType) {
                    1 -> {
                        viewModel.startDate = DateUtils.getLocalDateFromMillis(it)
                    }

                    2 -> {
                        viewModel.endDate = DateUtils.getLocalDateFromMillis(it)
                    }
                }
            }
        )
    }

    if (viewModel.openDeleteDialog) {
        DeleteMediaEntryDialog(viewModel = viewModel)
    }

    LaunchedEffect(viewModel.message) {
        if (viewModel.showMessage) {
            showToast(viewModel.message)
            viewModel.showMessage = false
        }
    }

    LaunchedEffect(mediaViewModel.mediaInfo) {
        viewModel.mediaInfo = mediaViewModel.mediaInfo
        mediaViewModel.myListStatus?.let {
            viewModel.setEditVariables(it)
        }
    }

    LaunchedEffect(viewModel.updateSuccess) {
        if (viewModel.updateSuccess) {
            mediaViewModel.myListStatus = viewModel.myListStatus
            viewModel.updateSuccess = false
            onDismiss()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp + bottomPadding)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(MR.strings.cancel))
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }

            Button(onClick = { viewModel.updateListItem() }) {
                Text(text = stringResource(if (isNewEntry) MR.strings.add else MR.strings.apply))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            statusValues.forEach { status ->
                SelectableIconToggleButton(
                    icon = status.icon(),
                    tooltipText = status.localized(),
                    value = status,
                    selectedValue = viewModel.status,
                    onClick = {
                        viewModel.onChangeStatus(status, isNewEntry)
                    }
                )
            }
        }

        EditMediaProgressRow(
            label = if (viewModel.mediaType == MediaType.ANIME) stringResource(MR.strings.episodes)
            else stringResource(MR.strings.chapters),
            progress = viewModel.progress,
            modifier = Modifier.padding(horizontal = 16.dp),
            totalProgress = viewModel.mediaInfo?.totalDuration(),
            onValueChange = { viewModel.onChangeProgress(it.toIntOrNull()) },
            onMinusClick = { viewModel.onChangeProgress(viewModel.progress?.minus(1)) },
            onPlusClick = { viewModel.onChangeProgress(viewModel.progress?.plus(1)) }
        )

        if (viewModel.mediaType == MediaType.MANGA) {
            EditMediaProgressRow(
                label = stringResource(MR.strings.volumes),
                progress = viewModel.volumeProgress,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                totalProgress = (viewModel.mediaInfo as MangaNode).numVolumes,
                onValueChange = { viewModel.onChangeVolumeProgress(it.toIntOrNull()) },
                onMinusClick = {
                    viewModel.onChangeVolumeProgress(
                        viewModel.volumeProgress?.minus(
                            1
                        )
                    )
                },
                onPlusClick = {
                    viewModel.onChangeVolumeProgress(
                        viewModel.volumeProgress?.plus(
                            1
                        )
                    )
                }
            )
        }

        Text(
            text = stringResource(MR.strings.score_value)
                .replace("%s", viewModel.score.scoreText()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            fontWeight = FontWeight.Bold
        )
        Slider(
            value = viewModel.score.toFloat(),
            onValueChange = { viewModel.score = it.toInt() },
            modifier = Modifier.padding(horizontal = 16.dp),
            valueRange = 0f..10f,
            steps = 10
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        ClickableOutlinedTextField(
            value = viewModel.startDate.toLocalized(),
            onValueChange = { },
            label = { Text(text = stringResource(MR.strings.start_date)) },
            trailingIcon = {
                if (viewModel.startDate != null) {
                    IconButton(onClick = { viewModel.startDate = null }) {
                        Icon(
                            painter = painterResource("outline_cancel_24"),
                            contentDescription = stringResource(MR.strings.delete)
                        )
                    }
                }
            },
            onClick = {
                datePickerState.setSelection(viewModel.startDate?.toEpochMillis())
                viewModel.selectedDateType = 1
                viewModel.openDatePicker = true
            }
        )
        ClickableOutlinedTextField(
            value = viewModel.endDate.toLocalized(),
            onValueChange = { },
            modifier = Modifier.padding(vertical = 8.dp),
            label = { Text(text = stringResource(MR.strings.end_date)) },
            trailingIcon = {
                if (viewModel.endDate != null) {
                    IconButton(onClick = { viewModel.endDate = null }) {
                        Icon(
                            painter = painterResource("outline_cancel_24"),
                            contentDescription = stringResource(MR.strings.delete)
                        )
                    }
                }
            },
            onClick = {
                datePickerState.setSelection(viewModel.endDate?.toEpochMillis())
                viewModel.selectedDateType = 2
                viewModel.openDatePicker = true
            }
        )

        EditMediaProgressRow(
            label = stringResource(
                if (viewModel.mediaType == MediaType.ANIME) MR.strings.total_rewatches
                else MR.strings.total_rereads
            ),
            progress = viewModel.repeatCount,
            modifier = Modifier.padding(16.dp),
            totalProgress = null,
            onValueChange = { viewModel.onChangeRepeatCount(it.toIntOrNull()) },
            onMinusClick = { viewModel.onChangeRepeatCount(viewModel.repeatCount - 1) },
            onPlusClick = { viewModel.onChangeRepeatCount(viewModel.repeatCount + 1) }
        )

        Button(
            onClick = { viewModel.openDeleteDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            enabled = !isNewEntry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text = stringResource(MR.strings.delete))
        }
    }//:Column
}

@Composable
fun DeleteMediaEntryDialog(viewModel: EditMediaViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.openDeleteDialog = false },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteEntry()
                    viewModel.openDeleteDialog = false
                }
            ) {
                Text(text = stringResource(MR.strings.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.openDeleteDialog = false }) {
                Text(text = stringResource(MR.strings.cancel))
            }
        },
        title = { Text(text = stringResource(MR.strings.delete)) },
        text = { Text(text = stringResource(MR.strings.delete_confirmation)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMediaDatePicker(
    viewModel: EditMediaViewModel,
    datePickerState: DatePickerState,
    onDateSelected: (Long) -> Unit
) {
    val dateConfirmEnabled by remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    /*DatePickerDialog(
        onDismissRequest = { viewModel.openDatePicker = false },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openDatePicker = false
                    onDateSelected(datePickerState.selectedDateMillis!!)
                },
                enabled = dateConfirmEnabled
            ) {
                Text(text = stringResource(MR.strings.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.openDatePicker = false }) {
                Text(text = stringResource(MR.strings.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }*/
}

@Composable
fun EditMediaProgressRow(
    label: String,
    progress: Int?,
    modifier: Modifier,
    totalProgress: Int?,
    onValueChange: (String) -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onMinusClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(MR.strings.minus_one))
        }
        OutlinedTextField(
            value = progress.toStringOrEmpty(),
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 16.dp),
            label = { Text(text = label) },
            suffix = {
                totalProgress?.let { Text(text = "/$it") }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedButton(
            onClick = onPlusClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(MR.strings.plus_one))
        }
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EditMediaSheetPreview() {
    MoeListTheme {
        EditMediaSheet(
            coroutineScope = rememberCoroutineScope(),
            sheetState = rememberModalBottomSheetState(),
            mediaViewModel = viewModel { MediaDetailsViewModel(MediaType.ANIME) }
        )
    }
}
*/