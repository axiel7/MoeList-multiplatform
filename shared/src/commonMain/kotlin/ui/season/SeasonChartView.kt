package ui.season

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import data.model.anime.Season
import data.model.anime.dayTimeText
import data.model.anime.icon
import data.model.anime.localized
import data.model.anime.seasonYearText
import data.model.media.durationText
import data.model.media.mediaFormatLocalized
import data.model.media.totalDuration
import data.model.media.userPreferredTitle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import showToast
import ui.composables.DefaultScaffoldWithTopAppBar
import ui.composables.MediaItemDetailed
import ui.composables.MediaItemDetailedPlaceholder
import ui.composables.OnBottomReached
import ui.composables.SelectableIconToggleButton
import utils.NumExtensions.toStringPositiveValueOrNull
import utils.NumExtensions.toStringPositiveValueOrUnknown

class SeasonChartScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val scaffoldState = rememberBottomSheetScaffoldState()
        val viewModel = rememberScreenModel { SeasonChartViewModel() }
        val scope = rememberCoroutineScope()
        val bottomBarPadding =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        BottomSheetScaffold(
            sheetContent = {
                SeasonChartFilterSheet(
                    viewModel = viewModel,
                    onDismissRequest = { scope.launch { scaffoldState.bottomSheetState.hide() } },
                    bottomPadding = bottomBarPadding
                )
            },
            scaffoldState = scaffoldState
        ) {
            val navigator = LocalNavigator.currentOrThrow
            val listState = rememberLazyListState()

            listState.OnBottomReached(buffer = 3) {
                if (!viewModel.isLoading && viewModel.hasNextPage) {
                    viewModel.getSeasonalAnime(viewModel.nextPage)
                }
            }

            LaunchedEffect(viewModel.message) {
                if (viewModel.showMessage) {
                    showToast(viewModel.message)
                    viewModel.showMessage = false
                }
            }

            LaunchedEffect(Unit) {
                if (viewModel.animes.isEmpty()) viewModel.getSeasonalAnime()
            }

            DefaultScaffoldWithTopAppBar(
                title = viewModel.season.seasonYearText(),
                navigateBack = { navigator.pop() },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            scope.launch { scaffoldState.bottomSheetState.show() }
                        },
                        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
                    ) {
                        Icon(
                            painter = painterResource("ic_round_filter_list_24"),
                            contentDescription = "filter"
                        )
                    }
                },
                contentWindowInsets = WindowInsets.systemBars
                    .only(WindowInsetsSides.Horizontal)
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    state = listState,
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = bottomBarPadding
                    )
                ) {
                    items(
                        items = viewModel.animes,
                        key = { it.node.id },
                        contentType = { it.node }
                    ) { item ->
                        MediaItemDetailed(
                            title = item.node.userPreferredTitle(),
                            imageUrl = item.node.mainPicture?.large,
                            subtitle1 = {
                                Text(
                                    text = buildString {
                                        append(item.node.mediaType?.mediaFormatLocalized())
                                        if (item.node.totalDuration()
                                                .toStringPositiveValueOrNull() != null
                                        ) {
                                            append(" (${item.node.durationText()})")
                                        }
                                    },
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            subtitle2 = {
                                Icon(
                                    painter = painterResource("ic_round_details_star_24"),
                                    contentDescription = "star",
                                    modifier = Modifier.padding(end = 4.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = item.node.mean.toStringPositiveValueOrUnknown(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            subtitle3 = {
                                Icon(
                                    painter = painterResource("ic_round_event_24"),
                                    contentDescription = "calendar",
                                    modifier = Modifier.padding(end = 4.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = item.node.broadcast.dayTimeText(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                TODO()
                                //navigateToMediaDetails(MediaType.ANIME, item.node.id)
                            }
                        )
                    }
                    if (viewModel.isLoading) {
                        items(10) {
                            MediaItemDetailedPlaceholder()
                        }
                    }
                }
            }//:Scaffold
        }//:BottomSheetScaffold
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonChartFilterSheet(
    viewModel: SeasonChartViewModel,
    onDismissRequest: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp + bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(MR.strings.cancel))
            }

            Button(
                onClick = {
                    viewModel.getSeasonalAnime()
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(MR.strings.apply))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Season.values().forEach { season ->
                SelectableIconToggleButton(
                    icon = season.icon(),
                    tooltipText = season.localized(),
                    value = season,
                    selectedValue = viewModel.season.season,
                    onClick = {
                        viewModel.setSeason(season = season)
                    }
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(viewModel.years) {
                FilterChip(
                    selected = viewModel.season.year == it,
                    onClick = { viewModel.setSeason(year = it) },
                    label = { Text(text = it.toString()) },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }//:Column
}

/*
@Preview(showBackground = true)
@Composable
fun SeasonChartPreview() {
    MoeListTheme {
        SeasonChartView(
            navigateBack = {},
            navigateToMediaDetails = { _, _ -> }
        )
    }
}
*/