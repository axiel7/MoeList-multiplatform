package ui.ranking

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import data.model.media.MediaType
import data.model.media.durationText
import data.model.media.localized
import data.model.media.mediaFormatLocalized
import data.model.media.rankingAnimeValues
import data.model.media.rankingMangaValues
import data.model.media.totalDuration
import data.model.media.userPreferredTitle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import showToast
import ui.base.TabRowItem
import ui.composables.DefaultScaffoldWithTopAppBar
import ui.composables.MediaItemDetailed
import ui.composables.MediaItemDetailedPlaceholder
import ui.composables.OnBottomReached
import ui.composables.RoundedTabRowIndicator
import utils.NumExtensions.toStringOrZero
import utils.NumExtensions.toStringPositiveValueOrNull
import utils.NumExtensions.toStringPositiveValueOrUnknown

@OptIn(ExperimentalFoundationApi::class)
class MediaRankingView(
    private val mediaType: MediaType,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        val tabRowItems = remember {
            (if (mediaType == MediaType.ANIME) rankingAnimeValues else rankingMangaValues)
                .map {
                    TabRowItem(value = it, title = it.value)
                }
        }
        val pagerState = rememberPagerState { tabRowItems.size }

        DefaultScaffoldWithTopAppBar(
            title = stringResource(
                if (mediaType == MediaType.ANIME) MR.strings.anime_ranking
                else MR.strings.manga_ranking
            ),
            navigateBack = { navigator.pop() },
            contentWindowInsets = WindowInsets.systemBars
                .only(WindowInsetsSides.Horizontal)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        RoundedTabRowIndicator(tabPositions[pagerState.currentPage])
                    },
                    //TODO: use default when width is fixed upstream
                    // https://issuetracker.google.com/issues/242879624
                    divider = { }
                ) {
                    tabRowItems.forEachIndexed { index, tabRowItem ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(text = tabRowItem.value.localized()) }
                        )
                    }
                }
                Divider()

                HorizontalPager(
                    state = pagerState,
                    beyondBoundsPageCount = 0,
                    key = { tabRowItems[it].title }
                ) {
                    val rankingType = tabRowItems[it].value
                    val viewModel = rememberScreenModel(tag = rankingType.value) {
                        MediaRankingViewModel(mediaType, rankingType)
                    }
                    MediaRankingListView(
                        viewModel = viewModel,
                        mediaType = mediaType,
                        navigateToMediaDetails = { mediaType, id ->
                            TODO("navigateToMediaDetails")
                        }
                    )
                }
            }
        }//:Scaffold
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MediaRankingListView(
    viewModel: MediaRankingViewModel,
    mediaType: MediaType,
    navigateToMediaDetails: (MediaType, Int) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (!viewModel.isLoading && viewModel.hasNextPage) {
            viewModel.getRanking(viewModel.nextPage)
        }
    }

    LaunchedEffect(viewModel.message) {
        if (viewModel.showMessage) {
            showToast(viewModel.message)
            viewModel.showMessage = false
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoading && viewModel.nextPage == null && !viewModel.loadedAllPages)
            viewModel.getRanking()
    }

    LazyColumn(
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ),
        state = listState
    ) {
        items(
            items = viewModel.mediaList,
            key = { it.node.id },
            contentType = { it.node }
        ) { item ->
            MediaItemDetailed(
                title = item.node.userPreferredTitle(),
                imageUrl = item.node.mainPicture?.large,
                badgeContent = {
                    Text(
                        text = "#${item.ranking?.rank}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                subtitle1 = {
                    Text(
                        text = buildString {
                            append(item.node.mediaType?.mediaFormatLocalized())
                            if (item.node.totalDuration().toStringPositiveValueOrNull() != null) {
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
                        painter = painterResource("ic_round_group_24"),
                        contentDescription = "group",
                        modifier = Modifier.padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.node.numListUsers.toStringOrZero(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    navigateToMediaDetails(mediaType, item.node.id)
                }
            )
        }
        if (viewModel.mediaList.isEmpty() && viewModel.isLoading) {
            items(10) {
                MediaItemDetailedPlaceholder()
            }
        }
    }//:LazyColumn
}

/*
@Preview(showBackground = true)
@Composable
fun MediaRankingPreview() {
    MoeListTheme {
        MediaRankingView(
            mediaType = MediaType.MANGA,
            navigateBack = {},
            navigateToMediaDetails = { _, _ -> }
        )
    }
}
*/