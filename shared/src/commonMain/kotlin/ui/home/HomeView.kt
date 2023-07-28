package ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.moelist.common.MR
import data.model.anime.AnimeRanking
import data.model.anime.airingInString
import data.model.anime.icon
import data.model.media.MediaType
import data.model.media.userPreferredTitle
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.base.SelectableTab
import ui.base.TabContent
import ui.composables.MEDIA_ITEM_VERTICAL_HEIGHT
import ui.composables.MEDIA_POSTER_SMALL_HEIGHT
import ui.composables.MEDIA_POSTER_SMALL_WIDTH
import ui.composables.MediaItemDetailedPlaceholder
import ui.composables.MediaItemVertical
import ui.composables.MediaItemVerticalPlaceholder
import ui.composables.MediaPoster
import ui.composables.SmallScoreIndicator
import ui.composables.collapsable
import utils.SeasonCalendar
import kotlin.random.Random

@OptIn(ExperimentalResourceApi::class)
class HomeTab(
    private val isLoggedIn: Boolean,
    private val topBarHeightPx: Float,
    private val topBarOffsetY: Animatable<Float, AnimationVector1D>,
    private val padding: PaddingValues,
) : SelectableTab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.title_home)
            val icon = painterResource("ic_outline_home_24")
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    override val iconSelected: Painter
        @Composable
        get() = painterResource("ic_outline_home_24")

    @Composable
    override fun Content() {
        TabContent(
            HomeScreen(isLoggedIn, topBarHeightPx, topBarOffsetY, padding)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
class HomeScreen(
    private val isLoggedIn: Boolean,
    private val topBarHeightPx: Float,
    private val topBarOffsetY: Animatable<Float, AnimationVector1D>,
    private val padding: PaddingValues,
) : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        //val context = LocalContext.current
        val viewModel = rememberScreenModel { HomeViewModel() }
        val scrollState = rememberScrollState()
        val airingListState = rememberLazyListState()
        val seasonListState = rememberLazyListState()
        val recommendListState = rememberLazyListState()

        LaunchedEffect(viewModel.message) {
            if (viewModel.showMessage) {
                //context.showToast(viewModel.message)
                viewModel.showMessage = false
            }
        }

        LaunchedEffect(isLoggedIn) {
            viewModel.initRequestChain(isLoggedIn)
        }

        Column(
            modifier = Modifier
                .collapsable(
                    state = scrollState,
                    topBarHeightPx = topBarHeightPx,
                    topBarOffsetY = topBarOffsetY,
                )
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            // Chips
            Row(
                modifier = Modifier.padding(top = 10.dp, start = 8.dp, end = 16.dp)
            ) {
                HomeCard(
                    text = stringResource(MR.strings.anime_ranking),
                    icon = "ic_round_movie_24",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        //navigateToRanking(MediaType.ANIME)
                    },
                )

                HomeCard(
                    text = stringResource(MR.strings.manga_ranking),
                    icon = "ic_round_menu_book_24",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        //navigateToRanking(MediaType.MANGA)
                    },
                )
            }

            Row(
                modifier = Modifier.padding(top = 10.dp, start = 8.dp, end = 16.dp)
            ) {
                HomeCard(
                    text = stringResource(MR.strings.seasonal_chart),
                    icon = SeasonCalendar.currentSeason.icon(),
                    modifier = Modifier.weight(1f),
                    onClick = {

                    },
                )

                HomeCard(
                    text = stringResource(MR.strings.calendar),
                    icon = "ic_round_event_24",
                    modifier = Modifier.weight(1f),
                    onClick = {

                    },
                )
            }

            // Airing
            HeaderHorizontalList(
                text = stringResource(MR.strings.today),
                onClick = {

                }
            )
            if (!viewModel.isLoading && viewModel.todayAnimes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MEDIA_POSTER_SMALL_HEIGHT.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(MR.strings.nothing_today),
                        textAlign = TextAlign.Center
                    )
                }
            } else LazyRow(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .sizeIn(minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp),
                state = airingListState,
                contentPadding = PaddingValues(horizontal = 8.dp),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = airingListState)
            ) {
                items(
                    items = viewModel.todayAnimes,
                    key = { it.node.id },
                    contentType = { it.node }
                ) {
                    AiringAnimeHorizontalItem(
                        item = it,
                        onClick = {
                            //navigateToMediaDetails(MediaType.ANIME, it.node.id)
                        }
                    )
                }
                if (viewModel.isLoading) {
                    items(5) {
                        MediaItemDetailedPlaceholder()
                    }
                }
            }

            // This Season
            HeaderHorizontalList(
                text = stringResource(MR.strings.this_season),
                onClick = {

                }
            )
            if (!viewModel.isLoading && viewModel.seasonAnimes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MEDIA_POSTER_SMALL_HEIGHT.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(MR.strings.error_server),
                        textAlign = TextAlign.Center
                    )
                }
            } else LazyRow(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .sizeIn(minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp),
                state = seasonListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = seasonListState)
            ) {
                items(viewModel.seasonAnimes,
                    key = { it.node.id },
                    contentType = { it.node }
                ) {
                    MediaItemVertical(
                        url = it.node.mainPicture?.large,
                        title = it.node.userPreferredTitle(),
                        modifier = Modifier.padding(end = 8.dp),
                        subtitle = {
                            SmallScoreIndicator(
                                score = it.node.mean,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {

                        }
                    )
                }
                if (viewModel.isLoading) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
            }

            //Recommended
            HeaderHorizontalList(stringResource(MR.strings.recommendations), onClick = { })
            if (!isLoggedIn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MEDIA_POSTER_SMALL_HEIGHT.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(MR.strings.please_login_to_use_this_feature),
                        textAlign = TextAlign.Center
                    )
                }
            } else if (!viewModel.isLoading && viewModel.recommendedAnimes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MEDIA_POSTER_SMALL_HEIGHT.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(MR.strings.no_recommendations),
                        textAlign = TextAlign.Center
                    )
                }
            } else LazyRow(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .sizeIn(minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp),
                state = recommendListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = recommendListState)
            ) {
                items(viewModel.recommendedAnimes,
                    key = { it.node.id },
                    contentType = { it.node }
                ) {
                    MediaItemVertical(
                        url = it.node.mainPicture?.large,
                        title = it.node.userPreferredTitle(),
                        modifier = Modifier.padding(end = 8.dp),
                        subtitle = {
                            SmallScoreIndicator(
                                score = it.node.mean,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {

                        }
                    )
                }
                if (viewModel.isLoading) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                //Random
                OutlinedButton(
                    onClick = {
                        val type = if (Random.nextBoolean()) MediaType.ANIME else MediaType.MANGA
                        val id = Random.nextInt(from = 0, until = 6000)
                        //navigateToMediaDetails(type, id)
                    }
                ) {
                    Icon(
                        painter = painterResource("ic_round_casino_24"),
                        contentDescription = stringResource(MR.strings.random),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                    )
                    Text(
                        text = stringResource(MR.strings.random),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun HomeCard(
    text: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.padding(start = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                fontSize = 15.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                lineHeight = 15.sp
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HeaderHorizontalList(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                painter = painterResource("ic_round_arrow_forward_24"),
                contentDescription = text
            )
        }
    }
}

@Composable
fun AiringAnimeHorizontalItem(item: AnimeRanking, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .sizeIn(maxWidth = 300.dp, minWidth = 250.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        MediaPoster(
            url = item.node.mainPicture?.large,
            modifier = Modifier.size(
                width = MEDIA_POSTER_SMALL_WIDTH.dp,
                height = MEDIA_POSTER_SMALL_HEIGHT.dp
            )
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = item.node.userPreferredTitle(),
                fontSize = 18.sp,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = item.node.broadcast?.airingInString() ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SmallScoreIndicator(
                score = item.node.mean
            )
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MoeListTheme {
        Surface {
            HomeView(
                isLoggedIn = false,
                navigateToMediaDetails = { _, _ -> },
                navigateToRanking = {},
                navigateToSeasonChart = {},
                navigateToCalendar = {},
                padding = PaddingValues(),
                topBarHeightPx = 0f,
                topBarOffsetY = remember { Animatable(0f) }
            )
        }
    }
}
*/
