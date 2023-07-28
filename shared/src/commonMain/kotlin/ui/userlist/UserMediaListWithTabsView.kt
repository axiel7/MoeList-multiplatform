package ui.userlist

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import data.model.media.ListType
import data.model.media.MediaType
import data.model.media.listStatusAnimeValues
import data.model.media.listStatusMangaValues
import data.model.media.localized
import kotlinx.coroutines.launch
import ui.base.TabRowItem
import ui.composables.RoundedTabRowIndicator
import ui.details.EditMediaSheet

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
class UserMediaListWithTabsView(
    private val mediaType: MediaType,
    private val topBarHeightPx: Float,
    private val topBarOffsetY: Animatable<Float, AnimationVector1D>,
    private val padding: PaddingValues,
) : Screen {

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val tabRowItems = remember {
            (if (mediaType == MediaType.ANIME) listStatusAnimeValues else listStatusMangaValues)
                .map {
                    TabRowItem(
                        value = it,
                        title = it.value
                    )
                }
        }
        val pagerState = rememberPagerState { tabRowItems.size }
        val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

        var listType by remember {
            mutableStateOf(ListType(status = tabRowItems[0].value, mediaType = mediaType))
        }
        val viewModel = rememberScreenModel(tag = listType.toString()) {
            UserMediaListViewModel(listType)
        }

        BottomSheetScaffold(
            sheetContent = {
                EditMediaSheet(
                    mediaViewModel = viewModel,
                    onDismiss = {
                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    },
                    bottomPadding = systemBarsPadding.calculateBottomPadding()
                )
            },
            scaffoldState = scaffoldState
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = padding.calculateTopPadding(),
                    )
                    .graphicsLayer {
                        val topPadding = padding.calculateTopPadding().value +
                                systemBarsPadding.calculateTopPadding().value +
                                systemBarsPadding.calculateBottomPadding().value

                        translationY = if (topBarOffsetY.value > -topPadding) topBarOffsetY.value
                        else -topPadding
                    }
            ) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        RoundedTabRowIndicator(tabPositions[pagerState.currentPage])
                    }
                ) {
                    tabRowItems.forEachIndexed { index, item ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(text = item.value.localized()) },
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    beyondBoundsPageCount = 0,
                    key = { tabRowItems[it].value }
                ) {
                    LaunchedEffect(it) {
                        listType = ListType(status = tabRowItems[it].value, mediaType = mediaType)
                    }
                    UserMediaListView(
                        viewModel = viewModel,
                        listType = listType,
                        modifier = Modifier.padding(
                            bottom = systemBarsPadding.calculateBottomPadding()
                        ),
                        navigateToMediaDetails = { _, _ ->
                            TODO()
                        },
                        topBarHeightPx = topBarHeightPx,
                        topBarOffsetY = topBarOffsetY,
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding() +
                                    systemBarsPadding.calculateBottomPadding()
                        ),
                        showEditSheet = {

                        }
                    )
                }//:Pager
            }//:Column
        }//:SheetScaffold
    }
}