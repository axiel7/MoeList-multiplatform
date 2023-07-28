package ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import data.model.media.MediaType
import dev.icerock.moko.resources.compose.stringResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import openActionUrl
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import showToast
import ui.composables.DefaultScaffoldWithTopAppBar
import ui.composables.DonutChart
import ui.composables.TextIconHorizontal
import ui.composables.TextIconVertical
import ui.composables.defaultPlaceholder
import utils.Constants
import utils.DateUtils.parseIsoDateAndLocalize
import utils.NumExtensions.toStringOrZero

class ProfileScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { ProfileViewModel() }
        val scrollState = rememberScrollState()

        LaunchedEffect(viewModel.message) {
            if (viewModel.showMessage) {
                showToast(viewModel.message)
                viewModel.showMessage = false
            }
        }

        LaunchedEffect(Unit) {
            if (viewModel.user == null) viewModel.getMyUser()
        }

        DefaultScaffoldWithTopAppBar(
            title = stringResource(MR.strings.title_profile),
            navigateBack = { navigator.pop() }
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KamelImage(
                        resource = asyncPainterResource(data = viewModel.profilePictureUrl ?: ""),
                        contentDescription = "profile",
                        onLoading = {
                            Image(
                                painter = painterResource("ic_round_account_circle_24"),
                                contentDescription = "account"
                            )
                        },
                        onFailure = {
                            Image(
                                painter = painterResource("ic_round_account_circle_24"),
                                contentDescription = "account"
                            )
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(100))
                            .size(100.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading)
                            .clickable {
                                TODO()
                                /*navigateToFullPoster(
                                    arrayOf(
                                        viewModel.profilePictureUrl ?: ""
                                    ).toNavArgument()
                                )*/
                            }
                    )

                    Column {
                        Text(
                            text = viewModel.user?.name ?: "Loading...",
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        viewModel.user?.location?.let { location ->
                            if (location.isNotBlank())
                                TextIconHorizontal(
                                    text = location,
                                    icon = "ic_round_location_on_24",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                        }

                        viewModel.user?.birthday?.let { birthday ->
                            TextIconHorizontal(
                                text = birthday.parseIsoDateAndLocalize() ?: "",
                                icon = "ic_round_cake_24",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        TextIconHorizontal(
                            text = if (viewModel.user?.joinedAt != null)
                                viewModel.user?.joinedAt!!.parseIsoDateAndLocalize() ?: ""
                            else "Loading...",
                            icon = "ic_round_access_time_24",
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading)
                        )
                    }
                }//:Row

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                //Stats
                UserStatsView(
                    viewModel = viewModel,
                    mediaType = MediaType.ANIME,
                    isLoading = viewModel.isLoading
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                UserStatsView(
                    viewModel = viewModel,
                    mediaType = MediaType.MANGA,
                    isLoading = viewModel.isLoadingManga
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                TextButton(
                    onClick = { openActionUrl(Constants.MAL_PROFILE_URL + viewModel.user?.name) },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = stringResource(MR.strings.view_profile_mal),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }//:Column
        }//:Scaffold
    }
}

@Composable
fun UserStatsView(
    viewModel: ProfileViewModel,
    mediaType: MediaType,
    isLoading: Boolean
) {
    Text(
        text = if (mediaType == MediaType.ANIME) stringResource(MR.strings.anime_stats)
        else stringResource(MR.strings.manga_stats),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DonutChart(
            stats = if (mediaType == MediaType.ANIME) viewModel.animeStats else viewModel.mangaStats,
            centerContent = {
                Text(
                    text = stringResource(MR.strings.total_entries)
                        .replace(
                            "%s",
                            if (mediaType == MediaType.ANIME)
                                viewModel.animeStats.value.sumOf { it.value.toInt() }.toString()
                            else viewModel.mangaStats.value.sumOf { it.value.toInt() }.toString()
                        ),
                    modifier = Modifier
                        .width(100.dp)
                        .defaultPlaceholder(visible = isLoading),
                    textAlign = TextAlign.Center
                )
            }
        )

        Column {
            (if (mediaType == MediaType.ANIME) viewModel.animeStats else viewModel.mangaStats).value
                .forEach {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(text = stringResource(it.title)) },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        icon = {
                            Text(
                                text = it.value.toInt().toString(),
                                modifier = Modifier.defaultPlaceholder(visible = isLoading)
                            )
                        },
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            borderColor = it.color
                        )
                    )
                }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TextIconVertical(
            text = if (mediaType == MediaType.ANIME)
                viewModel.user?.animeStatistics?.numDays.toStringOrZero()
            else viewModel.userMangaStats?.days.toStringOrZero(),
            icon = "ic_round_event_24",
            tooltip = stringResource(MR.strings.days),
            isLoading = isLoading
        )
        if (mediaType == MediaType.ANIME) {
            TextIconVertical(
                text = viewModel.user?.animeStatistics?.numEpisodes.toStringOrZero(),
                icon = "play_circle_outline_24",
                tooltip = stringResource(MR.strings.episodes),
                isLoading = isLoading
            )
        } else {
            TextIconVertical(
                text = viewModel.userMangaStats?.chaptersRead.toStringOrZero(),
                icon = "ic_round_menu_book_24",
                tooltip = stringResource(MR.strings.chapters),
                isLoading = isLoading
            )
            TextIconVertical(
                text = viewModel.userMangaStats?.volumesRead.toStringOrZero(),
                icon = "ic_outline_book_24",
                tooltip = stringResource(MR.strings.volumes),
                isLoading = isLoading
            )
        }

        TextIconVertical(
            text = if (mediaType == MediaType.ANIME)
                viewModel.user?.animeStatistics?.meanScore.toStringOrZero()
            else viewModel.userMangaStats?.meanScore.toStringOrZero(),
            icon = "ic_round_details_star_24",
            tooltip = stringResource(MR.strings.mean_score),
            isLoading = isLoading
        )
        TextIconVertical(
            text = if (mediaType == MediaType.ANIME)
                viewModel.user?.animeStatistics?.numTimesRewatched.toStringOrZero()
            else viewModel.userMangaStats?.repeat.toStringOrZero(),
            icon = "round_repeat_24",
            tooltip = if (mediaType == MediaType.ANIME) stringResource(MR.strings.rewatched)
            else stringResource(MR.strings.total_rereads),
            isLoading = isLoading
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    MoeListTheme {
        ProfileView(
            navigateBack = {},
            navigateToFullPoster = {}
        )
    }
}
*/