package ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import com.moriatsushi.insetsx.navigationBars
import com.moriatsushi.insetsx.systemBars
import data.model.media.WeekDay
import data.model.media.durationText
import data.model.media.localized
import data.model.media.mediaFormatLocalized
import data.model.media.totalDuration
import data.model.media.userPreferredTitle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import kotlinx.datetime.isoDayNumber
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.composables.DefaultScaffoldWithTopAppBar
import ui.composables.MediaItemDetailed
import ui.composables.MediaItemDetailedPlaceholder
import ui.composables.RoundedTabRowIndicator
import utils.NumExtensions.toStringPositiveValueOrNull
import utils.NumExtensions.toStringPositiveValueOrUnknown
import utils.SeasonCalendar

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
class CalendarScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { CalendarViewModel() }
        val pagerState = rememberPagerState(
            initialPage = SeasonCalendar.weekDay.isoDayNumber - 1
        )
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(viewModel.message) {
            if (viewModel.showMessage) {
                //context.showToast(viewModel.message)
                viewModel.showMessage = false
            }
        }

        LaunchedEffect(Unit) {
            if (viewModel.weekAnime[0].isEmpty())
                viewModel.getSeasonAnime()
        }

        DefaultScaffoldWithTopAppBar(
            title = stringResource(MR.strings.calendar),
            navigateBack = {
                navigator.pop()
            },
            contentWindowInsets = WindowInsets.systemBars
                .only(WindowInsetsSides.Horizontal)
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        RoundedTabRowIndicator(tabPositions[pagerState.currentPage])
                    }
                ) {
                    WeekDay.values().forEachIndexed { index, weekDay ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(text = weekDay.localized()) }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    pageCount = WeekDay.values().size
                ) { page ->
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    ) {
                        items(
                            items = viewModel.weekAnime[page],
                            contentType = { it }
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
                                        text = item.node.broadcast?.startTime ?: "??",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    //navigateToMediaDetails(MediaType.ANIME, item.node.id)
                                }
                            )
                        }
                        if (viewModel.isLoading) {
                            items(10) {
                                MediaItemDetailedPlaceholder()
                            }
                        }
                    }//:LazyColumn
                }//:Pager
            }//:Column
        }//:Scaffold
    }
}

/*
@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    MoeListTheme {
        CalendarView(
            navigateBack = {},
            navigateToMediaDetails = { _, _ -> }
        )
    }
}*/
