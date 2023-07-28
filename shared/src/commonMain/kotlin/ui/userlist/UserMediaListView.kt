package ui.userlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ui.details.EditMediaSheet
import com.moelist.common.MR
import data.datastore.PreferencesDataStore.GENERAL_LIST_STYLE_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.GRID_ITEMS_PER_ROW_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.USE_GENERAL_LIST_STYLE_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.rememberPreference
import data.model.anime.AnimeNode
import data.model.manga.MyMangaListStatus
import data.model.manga.UserMangaList
import data.model.manga.isUsingVolumeProgress
import data.model.media.ListStatus
import data.model.media.ListType
import data.model.media.MediaType
import data.model.media.animeListSortItems
import data.model.media.icon
import data.model.media.listStatusValues
import data.model.media.localized
import data.model.media.mangaListSortItems
import data.model.media.totalProgress
import data.model.media.userPreferredTitle
import data.model.media.userProgress
import dev.icerock.moko.resources.compose.stringResource
import generalListStyle
import getWindowSize
import gridItemsPerRow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import showToast
import ui.base.ListStyle
import ui.composables.AlertDialog
import ui.composables.MEDIA_POSTER_MEDIUM_WIDTH
import ui.composables.OnBottomReached
import ui.composables.collapsable
import useGeneralListStyle

@OptIn(ExperimentalMaterial3Api::class)
class UserMediaListHostView(
    private val mediaType: MediaType,
    private val topBarHeightPx: Float,
    private val topBarOffsetY: Animatable<Float, AnimationVector1D>,
    private val padding: PaddingValues,
) : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val scaffoldState = rememberBottomSheetScaffoldState()
        val scope = rememberCoroutineScope()
        val selectedStatus = rememberSaveable { mutableStateOf(listStatusValues(mediaType)[0]) }
        var showEditSheet = remember { false }
        val listType by remember {
            derivedStateOf { ListType(selectedStatus.value, mediaType) }
        }
        val viewModel = rememberScreenModel(tag = listType.toString()) {
            UserMediaListViewModel(listType)
        }
        val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        BottomSheetScaffold(
            sheetContent = {
                if (showEditSheet) {
                    EditMediaSheet(
                        mediaViewModel = viewModel,
                        onDismiss = {
                            showEditSheet = false
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        },
                        bottomPadding = bottomBarPadding
                    )
                } else {
                    ListStatusSheet(
                        mediaType = mediaType,
                        selectedStatus = selectedStatus,
                        onDismiss = {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        }
                    )
                }
            },
            scaffoldState = scaffoldState
        ) {
            var isFabVisible by remember { mutableStateOf(true) }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        if (available.y < -1) isFabVisible = false
                        else if (available.y > 1) isFabVisible = true
                        return Offset.Zero
                    }
                }
            }

            Scaffold(
                modifier = Modifier.padding(
                    bottom = padding.calculateBottomPadding()
                ),
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = isFabVisible,
                        modifier = Modifier.sizeIn(minWidth = 80.dp, minHeight = 56.dp),
                        enter = slideInVertically(initialOffsetY = { it * 2 }),
                        exit = slideOutVertically(targetOffsetY = { it * 2 }),
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = { scope.launch { scaffoldState.bottomSheetState.show() } }
                        ) {
                            Icon(
                                painter = painterResource(selectedStatus.value.icon()),
                                contentDescription = "status",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = selectedStatus.value.localized())
                        }
                    }
                },
                contentWindowInsets = WindowInsets.systemBars
                    .only(WindowInsetsSides.Horizontal)
            ) { childPadding ->
                UserMediaListView(
                    viewModel = viewModel,
                    listType = listType,
                    modifier = Modifier.padding(childPadding),
                    nestedScrollConnection = nestedScrollConnection,
                    navigateToMediaDetails = { _, _ ->
                        TODO("navigateToMediaDetails")
                    },
                    topBarHeightPx = topBarHeightPx,
                    topBarOffsetY = topBarOffsetY,
                    contentPadding = padding,
                    showEditSheet = {
                        showEditSheet = true
                        scope.launch { scaffoldState.bottomSheetState.show() }
                    }
                )
            }//:Scaffold
        }//:BottomSheetScaffold
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun Screen.UserMediaListView(
    viewModel: UserMediaListViewModel,
    listType: ListType,
    modifier: Modifier = Modifier,
    nestedScrollConnection: NestedScrollConnection? = null,
    navigateToMediaDetails: (MediaType, Int) -> Unit,
    topBarHeightPx: Float,
    topBarOffsetY: Animatable<Float, AnimationVector1D>,
    contentPadding: PaddingValues = PaddingValues(),
    showEditSheet: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    //val pullRefreshState =
    //    rememberPullRefreshState(viewModel.isLoading, { viewModel.getUserList() })

    val useGeneralListStyle by rememberPreference(
        USE_GENERAL_LIST_STYLE_PREFERENCE_KEY,
        useGeneralListStyle
    )
    val generalListStyle by rememberPreference(
        GENERAL_LIST_STYLE_PREFERENCE_KEY,
        generalListStyle.value
    )

    val listStyle = if (useGeneralListStyle) generalListStyle else viewModel.listTypeStyle

    if (viewModel.openSortDialog) {
        MediaListSortDialog(viewModel = viewModel)
    }

    LaunchedEffect(viewModel.message) {
        if (viewModel.showMessage) {
            showToast(viewModel.message)
            viewModel.showMessage = false
        }
    }

    LaunchedEffect(viewModel.listSort, listType) {
        if (!viewModel.isLoading && viewModel.nextPage == null && !viewModel.loadedAllPages)
            viewModel.getUserList()
    }

    Box(
        modifier = modifier
            .clipToBounds()
            //.pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        val listModifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopStart)
            .then(
                if (nestedScrollConnection != null)
                    Modifier.nestedScroll(nestedScrollConnection)
                else Modifier
            )

        if (listStyle == ListStyle.GRID.value) {
            val itemsPerRow by rememberPreference(GRID_ITEMS_PER_ROW_PREFERENCE_KEY, gridItemsPerRow)
            val listState = rememberLazyGridState()
            if (!viewModel.isLoadingList) {
                listState.OnBottomReached(buffer = 3) {
                    if (viewModel.hasNextPage) {
                        viewModel.getUserList(viewModel.nextPage)
                    }
                }
            }
            LazyVerticalGrid(
                columns = if (itemsPerRow > 0) GridCells.Fixed(itemsPerRow)
                else GridCells.Adaptive(minSize = (MEDIA_POSTER_MEDIUM_WIDTH + 8).dp),
                modifier = listModifier
                    .collapsable(
                        state = listState,
                        topBarHeightPx = topBarHeightPx,
                        topBarOffsetY = topBarOffsetY,
                    ),
                state = listState,
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(layoutDirection) + 8.dp,
                    top = contentPadding.calculateTopPadding() + 8.dp,
                    end = contentPadding.calculateEndPadding(layoutDirection) + 8.dp,
                    bottom = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                item(
                    span = { GridItemSpan(maxCurrentLineSpan) }
                ) {
                    Row {
                        AssistChip(
                            onClick = { viewModel.openSortDialog = true },
                            label = { Text(text = viewModel.listSort.localized()) },
                            modifier = Modifier.padding(horizontal = 8.dp),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource("ic_round_sort_24"),
                                    contentDescription = stringResource(MR.strings.sort_by)
                                )
                            }
                        )
                    }
                }
                items(
                    items = viewModel.mediaList,
                    key = { it.node.id },
                    contentType = { it.node }
                ) { item ->
                    GridUserMediaListItem(
                        imageUrl = item.node.mainPicture?.large,
                        title = item.node.userPreferredTitle(),
                        score = item.listStatus?.score,
                        mediaStatus = item.node.status,
                        broadcast = (item.node as? AnimeNode)?.broadcast,
                        userProgress = item.userProgress(),
                        totalProgress = item.totalProgress(),
                        isVolumeProgress = (item as? UserMangaList)?.listStatus?.isUsingVolumeProgress()
                            ?: false,
                        onClick = {
                            navigateToMediaDetails(listType.mediaType, item.node.id)
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.onItemSelected(item)
                            showEditSheet()
                        }
                    )
                }
                if (viewModel.isLoadingList) {
                    items(9, contentType = { it }) {
                        GridUserMediaListItemPlaceholder()
                    }
                }
            }
        } else {
            val listState = rememberLazyListState()
            if (!viewModel.isLoadingList) {
                listState.OnBottomReached(buffer = 3) {
                    if (viewModel.hasNextPage) {
                        viewModel.getUserList(viewModel.nextPage)
                    }
                }
            }

            LazyColumn(
                modifier = listModifier
                    .collapsable(
                        state = listState,
                        topBarHeightPx = topBarHeightPx,
                        topBarOffsetY = topBarOffsetY,
                    ),
                state = listState,
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    top = contentPadding.calculateTopPadding() + 8.dp,
                    end = contentPadding.calculateEndPadding(layoutDirection),
                    bottom = 8.dp
                ),
            ) {
                item {
                    AssistChip(
                        onClick = { viewModel.openSortDialog = true },
                        label = { Text(text = viewModel.listSort.localized()) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource("ic_round_sort_24"),
                                contentDescription = stringResource(MR.strings.sort_by)
                            )
                        }
                    )
                }
                when (listStyle) {
                    ListStyle.STANDARD.value -> {
                        items(
                            items = viewModel.mediaList,
                            key = { it.node.id },
                            contentType = { it.node }
                        ) { item ->
                            StandardUserMediaListItem(
                                imageUrl = item.node.mainPicture?.large,
                                title = item.node.userPreferredTitle(),
                                score = item.listStatus?.score,
                                mediaFormat = item.node.mediaType,
                                mediaStatus = item.node.status,
                                broadcast = (item.node as? AnimeNode)?.broadcast,
                                userProgress = item.userProgress(),
                                totalProgress = item.totalProgress(),
                                isVolumeProgress = (item as? UserMangaList)?.listStatus?.isUsingVolumeProgress()
                                    ?: false,
                                listStatus = listType.status,
                                onClick = {
                                    navigateToMediaDetails(listType.mediaType, item.node.id)
                                },
                                onLongClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.onItemSelected(item)
                                    showEditSheet()
                                },
                                onClickPlus = {
                                    val isVolumeProgress =
                                        (item as? UserMangaList)?.listStatus?.isUsingVolumeProgress()
                                            ?: false
                                    viewModel.updateListItem(
                                        mediaId = item.node.id,
                                        progress = if (!isVolumeProgress)
                                            item.listStatus?.progress?.plus(1) else null,
                                        volumeProgress = if (isVolumeProgress)
                                                (item.listStatus as? MyMangaListStatus)
                                                    ?.numVolumesRead?.plus(1) else null
                                    )
                                }
                            )
                        }
                        if (viewModel.isLoadingList) {
                            items(5, contentType = { it }) {
                                StandardUserMediaListItemPlaceholder()
                            }
                        }
                    }

                    ListStyle.COMPACT.value -> {
                        items(
                            items = viewModel.mediaList,
                            key = { it.node.id },
                            contentType = { it.node }
                        ) { item ->
                            CompactUserMediaListItem(
                                imageUrl = item.node.mainPicture?.large,
                                title = item.node.userPreferredTitle(),
                                score = item.listStatus?.score,
                                userProgress = item.userProgress(),
                                totalProgress = item.totalProgress(),
                                isVolumeProgress = (item as? UserMangaList)?.listStatus?.isUsingVolumeProgress()
                                    ?: false,
                                mediaStatus = item.node.status,
                                broadcast = (item.node as? AnimeNode)?.broadcast,
                                listStatus = listType.status,
                                onClick = {
                                    navigateToMediaDetails(listType.mediaType, item.node.id)
                                },
                                onLongClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.onItemSelected(item)
                                    showEditSheet()
                                },
                                onClickPlus = {
                                    val isVolumeProgress =
                                        (item as? UserMangaList)?.listStatus?.isUsingVolumeProgress()
                                            ?: false
                                    viewModel.updateListItem(
                                        mediaId = item.node.id,
                                        progress = if (!isVolumeProgress) item.listStatus?.progress?.plus(
                                            1
                                        ) else null,
                                        volumeProgress = if (isVolumeProgress) (item.listStatus as? MyMangaListStatus)
                                            ?.numVolumesRead?.plus(1) else null
                                    )
                                }
                            )
                        }
                        if (viewModel.isLoadingList) {
                            items(5, contentType = { it }) {
                                CompactUserMediaListItemPlaceholder()
                            }
                        }
                    }

                    ListStyle.MINIMAL.value -> {
                        items(
                            items = viewModel.mediaList,
                            key = { it.node.id },
                            contentType = { it.node }
                        ) { item ->
                            MinimalUserMediaListItem(
                                title = item.node.userPreferredTitle(),
                                score = item.listStatus?.score,
                                userProgress = item.userProgress(),
                                totalProgress = item.totalProgress(),
                                isVolumeProgress = (item as? UserMangaList)?.listStatus?.isUsingVolumeProgress()
                                    ?: false,
                                mediaStatus = item.node.status,
                                broadcast = (item.node as? AnimeNode)?.broadcast,
                                listStatus = listType.status,
                                onClick = {
                                    navigateToMediaDetails(listType.mediaType, item.node.id)
                                },
                                onLongClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.onItemSelected(item)
                                    showEditSheet()
                                },
                                onClickPlus = {
                                    val isVolumeProgress =
                                        (item as? UserMangaList)?.listStatus?.isUsingVolumeProgress()
                                            ?: false
                                    viewModel.updateListItem(
                                        mediaId = item.node.id,
                                        progress = if (!isVolumeProgress) item.listStatus?.progress?.plus(
                                            1
                                        ) else null,
                                        volumeProgress = if (isVolumeProgress) (item.listStatus as? MyMangaListStatus)
                                            ?.numVolumesRead?.plus(1) else null
                                    )
                                }
                            )
                        }
                        if (viewModel.isLoadingList) {
                            items(5, contentType = { it }) {
                                MinimalUserMediaListItemPlaceholder()
                            }
                        }
                    }
                }
            }//:LazyColumn
        }

        /*PullRefreshIndicator(
            refreshing = viewModel.isLoading,
            state = pullRefreshState,
            modifier = Modifier
                .padding(top = contentPadding.calculateTopPadding())
                .align(Alignment.TopCenter)
        )*/
    }//:Box
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ListStatusSheet(
    mediaType: MediaType,
    selectedStatus: MutableState<ListStatus>,
    onDismiss: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        listStatusValues(mediaType).forEach {
            val isSelected = selectedStatus.value == it
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedStatus.value = it
                        onDismiss()
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(it.icon()),
                    contentDescription = "check",
                    tint = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = it.localized(),
                    modifier = Modifier.padding(start = 8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }//:Column
}

@Composable
fun MediaListSortDialog(
    viewModel: UserMediaListViewModel
) {
    val sortOptions = remember {
        if (viewModel.mediaType == MediaType.ANIME) animeListSortItems else mangaListSortItems
    }
    var selectedIndex by remember {
        mutableIntStateOf(sortOptions.indexOf(viewModel.listSort))
    }
    AlertDialog(
        onDismissRequest = { viewModel.openSortDialog = false },
        confirmButton = {
            TextButton(onClick = {
                viewModel.setSort(sortOptions[selectedIndex])
                viewModel.openSortDialog = false
            }) {
                Text(text = stringResource(MR.strings.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.openSortDialog = false }) {
                Text(text = stringResource(MR.strings.cancel))
            }
        },
        title = { Text(text = stringResource(MR.strings.sort_by)) },
        text = {
            LazyColumn(
                modifier = Modifier.sizeIn(
                    maxHeight = getWindowSize().height - 48.dp
                )
            ) {
                itemsIndexed(sortOptions) { index, sort ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedIndex = index },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index }
                        )
                        Text(text = sort.localized())
                    }
                }
            }
        }
    )
}

/*
@Preview(showBackground = true)
@Composable
fun UserMediaListHostPreview() {
    MoeListTheme {
        UserMediaListHostView(
            mediaType = MediaType.ANIME,
            navigateToMediaDetails = { _, _ -> },
            topBarHeightPx = 0f,
            topBarOffsetY = remember { Animatable(0f) },
            padding = PaddingValues(),
        )
    }
}
*/