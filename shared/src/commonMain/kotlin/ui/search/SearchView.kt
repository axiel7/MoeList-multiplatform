package ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.moelist.common.MR
import data.model.anime.AnimeList
import data.model.anime.seasonYearText
import data.model.manga.MangaList
import data.model.media.MediaType
import data.model.media.durationText
import data.model.media.mediaFormatLocalized
import data.model.media.totalDuration
import data.model.media.userPreferredTitle
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import showToast
import ui.composables.MediaItemDetailed
import ui.composables.MediaItemDetailedPlaceholder
import ui.composables.OnBottomReached
import utils.NumExtensions.toStringPositiveValueOrNull
import utils.NumExtensions.toStringPositiveValueOrUnknown

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun Screen.SearchView(
    query: String,
    performSearch: MutableState<Boolean>,
    navigateToMediaDetails: (MediaType, Int) -> Unit,
) {
    val viewModel = rememberScreenModel { SearchViewModel() }
    val listState = rememberLazyListState()
    var mediaType by remember { mutableStateOf(MediaType.ANIME) }

    listState.OnBottomReached(buffer = 3) {
        if (!viewModel.isLoading && viewModel.hasNextPage) {
            viewModel.search(
                mediaType = mediaType,
                query = query,
                page = viewModel.nextPage
            )
        }
    }

    LaunchedEffect(viewModel.message) {
        if (viewModel.showMessage) {
            showToast(viewModel.message)
            viewModel.showMessage = false
        }
    }

    LaunchedEffect(mediaType, performSearch.value) {
        if (query.isNotBlank() && performSearch.value) {
            viewModel.search(
                mediaType = mediaType,
                query = query
            )
            performSearch.value = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState
    ) {
        item {
            Row {
                FilterChip(
                    selected = mediaType == MediaType.ANIME,
                    onClick = {
                        mediaType = MediaType.ANIME
                        if (query.isNotBlank()) performSearch.value = true
                    },
                    label = { Text(text = stringResource(MR.strings.anime)) },
                    modifier = Modifier.padding(start = 8.dp),
                    leadingIcon = {
                        if (mediaType == MediaType.ANIME) {
                            Icon(
                                painter = painterResource("round_check_24"),
                                contentDescription = "check"
                            )
                        }
                    }
                )
                FilterChip(
                    selected = mediaType == MediaType.MANGA,
                    onClick = {
                        mediaType = MediaType.MANGA
                        if (query.isNotBlank()) performSearch.value = true
                    },
                    label = { Text(text = stringResource(MR.strings.manga)) },
                    modifier = Modifier.padding(start = 8.dp),
                    leadingIcon = {
                        if (mediaType == MediaType.MANGA) {
                            Icon(
                                painter = painterResource("round_check_24"),
                                contentDescription = "check"
                            )
                        }
                    }
                )
            }
        }
        items(
            items = viewModel.mediaList,
            contentType = { it.node }
        ) {
            MediaItemDetailed(
                title = it.node.userPreferredTitle(),
                imageUrl = it.node.mainPicture?.large,
                subtitle1 = {
                    Text(
                        text = buildString {
                            append(it.node.mediaType?.mediaFormatLocalized())
                            if (it.node.totalDuration().toStringPositiveValueOrNull() != null) {
                                append(" (${it.node.durationText()})")
                            }
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                subtitle2 = {
                    Text(
                        text = when (it) {
                            is AnimeList -> it.node.startSeason.seasonYearText()
                            is MangaList -> it.node.startDate ?: stringResource(MR.strings.unknown)
                            else -> stringResource(MR.strings.unknown)
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                subtitle3 = {
                    Icon(
                        painter = painterResource("ic_round_details_star_24"),
                        contentDescription = "star",
                        modifier = Modifier.padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = it.node.mean.toStringPositiveValueOrUnknown(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    navigateToMediaDetails(mediaType, it.node.id)
                }
            )
        }
        if (query.isNotBlank() && viewModel.mediaList.isEmpty()) {
            if (viewModel.isLoading) {
                items(10) {
                    MediaItemDetailedPlaceholder()
                }
            } else if (performSearch.value) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(MR.strings.no_results),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }//: LazyColumn
}

/*
@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    MoeListTheme {
        SearchView(
            query = "one",
            performSearch = remember { mutableStateOf(false) },
            navigateToMediaDetails = { _, _ -> }
        )
    }
}
*/