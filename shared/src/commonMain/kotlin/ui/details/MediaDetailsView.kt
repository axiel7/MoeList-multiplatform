package ui.details

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.axiel7.moelist.R
import com.axiel7.moelist.data.datastore.PreferencesDataStore.notificationsDataStore
import com.axiel7.moelist.data.model.anime.AnimeDetails
import com.axiel7.moelist.data.model.anime.broadcastTimeText
import com.axiel7.moelist.data.model.anime.episodeDurationLocalized
import com.axiel7.moelist.data.model.anime.seasonYearText
import com.axiel7.moelist.data.model.anime.sourceLocalized
import com.axiel7.moelist.data.model.manga.MangaDetails
import com.axiel7.moelist.data.model.media.MediaType
import com.axiel7.moelist.data.model.media.durationText
import com.axiel7.moelist.data.model.media.mediaFormatLocalized
import com.axiel7.moelist.data.model.media.nameLocalized
import com.axiel7.moelist.data.model.media.rankText
import com.axiel7.moelist.data.model.media.relationLocalized
import com.axiel7.moelist.data.model.media.statusLocalized
import com.axiel7.moelist.data.model.media.synonymsJoined
import com.axiel7.moelist.data.model.media.synopsisAndBackground
import com.axiel7.moelist.data.model.media.userPreferredTitle
import ui.composables.BackIconButton
import ui.composables.InfoTitle
import ui.composables.MEDIA_POSTER_BIG_HEIGHT
import ui.composables.MEDIA_POSTER_BIG_WIDTH
import ui.composables.MediaItemVertical
import ui.composables.MediaPoster
import ui.composables.ShareButton
import ui.composables.TextIconHorizontal
import ui.composables.TextIconVertical
import ui.composables.VerticalDivider
import ui.composables.ViewInBrowserButton
import ui.composables.defaultPlaceholder
import ui.theme.MoeListTheme
import com.axiel7.moelist.utils.Constants
import com.axiel7.moelist.utils.ContextExtensions.getCurrentLanguageTag
import com.axiel7.moelist.utils.ContextExtensions.openAction
import com.axiel7.moelist.utils.ContextExtensions.openInGoogleTranslate
import com.axiel7.moelist.utils.ContextExtensions.openLink
import com.axiel7.moelist.utils.ContextExtensions.showToast
import com.axiel7.moelist.utils.DateUtils.parseDate
import com.axiel7.moelist.utils.DateUtils.parseDateAndLocalize
import com.axiel7.moelist.utils.NotificationWorker
import com.axiel7.moelist.utils.NumExtensions
import com.axiel7.moelist.utils.NumExtensions.toStringPositiveValueOrNull
import com.axiel7.moelist.utils.StringExtensions.toNavArgument
import com.axiel7.moelist.utils.StringExtensions.toStringOrNull
import com.axiel7.moelist.utils.UseCases.copyToClipBoard
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import data.model.media.MediaType
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ui.details.EditMediaSheet
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class MediaDetailsView(
    private val mediaType: MediaType,
    private val mediaId: Int,
    private val isLoggedIn: Boolean,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { MediaDetailsViewModel(mediaType) }

        val scrollState = rememberScrollState()
        val topAppBarScrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        val coroutineScope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()
        val bottomBarPadding =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        var maxLinesSynopsis by remember { mutableIntStateOf(5) }
        var iconExpand by remember { mutableIntStateOf(R.drawable.ic_round_keyboard_arrow_down_24) }
        val isNewEntry by remember {
            derivedStateOf { viewModel.mediaDetails?.myListStatus == null }
        }
        val isCurrentLanguageEn = remember { getCurrentLanguageTag()?.startsWith("en") }
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        } else null

        

        if (sheetState.isVisible) {
            EditMediaSheet(
                coroutineScope = coroutineScope,
                sheetState = sheetState,
                mediaViewModel = viewModel,
                bottomPadding = bottomBarPadding
            )
        }

        LaunchedEffect(viewModel.message) {
            if (viewModel.showMessage) {
                context.showToast(viewModel.message)
                viewModel.showMessage = false
            }
        }

        LaunchedEffect(mediaId) {
            if (viewModel.mediaDetails == null) viewModel.getDetails(mediaId)
        }

        Scaffold(
            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            topBar = {
                MediaDetailsTopAppBar(
                    viewModel = viewModel,
                    mediaUrl = if (mediaType == MediaType.ANIME) Constants.ANIME_URL + mediaId
                    else Constants.MANGA_URL + mediaId,
                    navigateBack = navigateBack,
                    scrollBehavior = topAppBarScrollBehavior,
                    onClickNotification = { enable ->
                        (viewModel.mediaDetails as? AnimeDetails)?.let { details ->
                            if (enable) {
                                if (notificationPermission == null
                                    || notificationPermission.status.isGranted
                                ) {
                                    coroutineScope.launch {
                                        if (details.status != "not_yet_aired"
                                            && details.broadcast?.dayOfTheWeek != null
                                            && details.broadcast.startTime != null
                                        ) {
                                            NotificationWorker.scheduleAiringAnimeNotification(
                                                context = context,
                                                title = details.title ?: "",
                                                animeId = details.id,
                                                weekDay = details.broadcast.dayOfTheWeek,
                                                jpHour = LocalTime.parse(details.broadcast.startTime)
                                            )
                                            context.showToast(MR.strings.airing_notification_enabled)
                                        } else if (details.status == "not_yet_aired"
                                            && details.startDate != null
                                        ) {
                                            val startDate = details.startDate.parseDate()
                                            if (startDate != null) {
                                                NotificationWorker.scheduleAnimeStartNotification(
                                                    context = context,
                                                    title = details.title ?: "",
                                                    animeId = details.id,
                                                    startDate = startDate
                                                )
                                                context.showToast(MR.strings.start_airing_notification_enabled)
                                            } else {
                                                context.showToast(MR.strings.invalid_start_date)
                                            }
                                        } else {
                                            if (details.broadcast?.dayOfTheWeek == null
                                                || details.broadcast.startTime == null
                                            ) {
                                                context.showToast(MR.strings.invalid_broadcast)
                                            } else if (details.startDate == null) {
                                                context.showToast(MR.strings.invalid_start_date)
                                            }
                                        }
                                    }
                                } else {
                                    notificationPermission.launchPermissionRequest()
                                }
                            } else {
                                coroutineScope.launch {
                                    NotificationWorker.removeAiringAnimeNotification(
                                        context = context,
                                        animeId = details.id
                                    )
                                    context.showToast("Notification disabled")
                                }
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (isLoggedIn) {
                            if (viewModel.mediaDetails != null) coroutineScope.launch { sheetState.show() }
                        } else context.showToast(context.getString(MR.strings.please_login_to_use_this_feature))
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (isNewEntry) R.drawable.ic_round_add_24
                            else R.drawable.ic_round_edit_24
                        ),
                        contentDescription = "edit"
                    )
                    Text(
                        text = stringResource(if (isNewEntry) MR.strings.add else MR.strings.edit),
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(bottom = 88.dp)
            ) {
                Row {
                    MediaPoster(
                        url = viewModel.mediaDetails?.mainPicture?.large,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .size(
                                width = MEDIA_POSTER_BIG_WIDTH.dp,
                                height = MEDIA_POSTER_BIG_HEIGHT.dp
                            )
                            .defaultPlaceholder(visible = viewModel.isLoading)
                            .clickable {
                                navigateToFullPoster(viewModel.picturesUrls.toNavArgument())
                            }
                    )
                    Column {
                        Text(
                            text = viewModel.mediaDetails?.userPreferredTitle() ?: "Loading",
                            modifier = Modifier
                                .padding(bottom = 8.dp, end = 8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading)
                                .combinedClickable(
                                    onLongClick = {
                                        viewModel.mediaDetails?.title?.let {
                                            context.copyToClipBoard(
                                                it
                                            )
                                        }
                                    },
                                    onClick = { }
                                ),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextIconHorizontal(
                            text = viewModel.mediaDetails?.mediaType?.mediaFormatLocalized()
                                ?: "Loading",
                            icon = if (mediaType == MediaType.ANIME) R.drawable.ic_round_movie_24
                            else R.drawable.ic_round_menu_book_24,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading)
                        )
                        TextIconHorizontal(
                            text = viewModel.mediaDetails?.durationText() ?: "Loading",
                            icon = R.drawable.ic_round_timer_24,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading)
                        )
                        TextIconHorizontal(
                            text = viewModel.mediaDetails?.status?.statusLocalized() ?: "Loading",
                            icon = R.drawable.ic_round_rss_feed_24,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading)
                        )
                        TextIconHorizontal(
                            text = viewModel.mediaDetails?.mean.toStringOrNull() ?: "??",
                            icon = R.drawable.ic_round_details_star_24,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading)
                        )
                    }
                }//:Row

                //Genres
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    viewModel.mediaDetails?.genres?.let { genres ->
                        items(genres) {
                            AssistChip(
                                onClick = { },
                                label = { Text(text = it.nameLocalized()) },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                //Synopsis
                Text(
                    text = viewModel.mediaDetails?.synopsisAndBackground()
                        ?: AnnotatedString(stringResource(MR.strings.lorem_ipsun)),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .defaultPlaceholder(visible = viewModel.isLoading),
                    lineHeight = 20.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = maxLinesSynopsis
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isCurrentLanguageEn == false) {
                        IconButton(onClick = {
                            viewModel.mediaDetails?.synopsis?.let { context.openInGoogleTranslate(it) }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_outline_translate_24),
                                contentDescription = stringResource(MR.strings.translate)
                            )
                        }
                    } else Spacer(modifier = Modifier.size(48.dp))

                    IconButton(
                        onClick = {
                            if (maxLinesSynopsis == 5) {
                                maxLinesSynopsis = Int.MAX_VALUE
                                iconExpand = R.drawable.ic_round_keyboard_arrow_up_24
                            } else {
                                maxLinesSynopsis = 5
                                iconExpand = R.drawable.ic_round_keyboard_arrow_down_24
                            }
                        }
                    ) {
                        Icon(painter = painterResource(iconExpand), contentDescription = "expand")
                    }

                    IconButton(
                        onClick = {
                            viewModel.mediaDetails?.synopsis?.let { context.copyToClipBoard(it) }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.round_content_copy_24),
                            contentDescription = "copy"
                        )
                    }
                }

                //Stats
                InfoTitle(text = stringResource(MR.strings.stats))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .defaultPlaceholder(visible = viewModel.isLoading),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextIconVertical(
                        text = viewModel.mediaDetails?.rankText() ?: "",
                        icon = R.drawable.ic_round_bar_chart_24,
                        tooltip = stringResource(MR.strings.top_ranked)
                    )
                    VerticalDivider(modifier = Modifier.height(32.dp))

                    TextIconVertical(
                        text = NumExtensions.numberFormat.format(
                            viewModel.mediaDetails?.numScoringUsers ?: 0
                        ),
                        icon = R.drawable.ic_round_thumbs_up_down_24,
                        tooltip = stringResource(MR.strings.users_scores)
                    )
                    VerticalDivider(modifier = Modifier.height(32.dp))

                    TextIconVertical(
                        text = NumExtensions.numberFormat.format(
                            viewModel.mediaDetails?.numListUsers ?: 0
                        ),
                        icon = R.drawable.ic_round_group_24,
                        tooltip = stringResource(MR.strings.members)
                    )
                    VerticalDivider(modifier = Modifier.height(32.dp))

                    TextIconVertical(
                        text = "# ${viewModel.mediaDetails?.popularity}",
                        icon = R.drawable.ic_round_trending_up_24,
                        tooltip = stringResource(MR.strings.popularity)
                    )
                }//:Row

                //Info
                InfoTitle(text = stringResource(MR.strings.more_info))
                if (mediaType == MediaType.MANGA) {
                    SelectionContainer {
                        MediaInfoView(
                            title = stringResource(MR.strings.authors),
                            info = (viewModel.mediaDetails as? MangaDetails)?.authors
                                ?.joinToString { "${it.node.firstName} ${it.node.lastName}" },
                            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                        )
                    }
                    MediaInfoView(
                        title = stringResource(MR.strings.volumes),
                        info = (viewModel.mediaDetails as? MangaDetails)?.numVolumes.toStringPositiveValueOrNull(),
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
                viewModel.mediaDetails?.synonymsJoined()?.let { synonyms ->
                    SelectionContainer {
                        MediaInfoView(
                            title = stringResource(MR.strings.synonyms),
                            info = synonyms,
                            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                        )
                    }
                }
                SelectionContainer {
                    MediaInfoView(
                        title = stringResource(MR.strings.jp_title),
                        info = viewModel.mediaDetails?.alternativeTitles?.ja,
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                }
                SelectionContainer {
                    MediaInfoView(
                        title = stringResource(MR.strings.romaji),
                        info = viewModel.mediaDetails?.title,
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                }
                SelectionContainer {
                    MediaInfoView(
                        title = stringResource(MR.strings.english),
                        info = viewModel.mediaDetails?.alternativeTitles?.en,
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                MediaInfoView(
                    title = stringResource(MR.strings.start_date),
                    info = viewModel.mediaDetails?.startDate?.parseDateAndLocalize(),
                    modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                )
                MediaInfoView(
                    title = stringResource(MR.strings.end_date),
                    info = viewModel.mediaDetails?.endDate?.parseDateAndLocalize(),
                    modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                )
                if (mediaType == MediaType.ANIME) {
                    MediaInfoView(
                        title = stringResource(MR.strings.season),
                        info = (viewModel.mediaDetails as? AnimeDetails)?.startSeason.seasonYearText(),
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                    MediaInfoView(
                        title = stringResource(MR.strings.broadcast),
                        info = (viewModel.mediaDetails as? AnimeDetails)?.broadcastTimeText(),
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                    MediaInfoView(
                        title = stringResource(MR.strings.duration),
                        info = (viewModel.mediaDetails as? AnimeDetails)?.episodeDurationLocalized(),
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                    MediaInfoView(
                        title = stringResource(MR.strings.source),
                        info = (viewModel.mediaDetails as? AnimeDetails)?.sourceLocalized(),
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SelectionContainer {
                    MediaInfoView(
                        title = stringResource(
                            id = if (mediaType == MediaType.ANIME) MR.strings.studios else MR.strings.serialization
                        ),
                        info = viewModel.studioSerializationJoined,
                        modifier = Modifier
                            .defaultPlaceholder(visible = viewModel.isLoading)
                            .padding(bottom = 8.dp)
                    )
                }

                //Themes
                if (mediaType == MediaType.ANIME) {
                    (viewModel.mediaDetails as? AnimeDetails)?.openingThemes?.let { themes ->
                        InfoTitle(text = stringResource(MR.strings.opening))
                        themes.forEach { theme ->
                            AnimeThemeItem(text = theme.text, onClick = {
                                context.openAction(
                                    Constants.YOUTUBE_QUERY_URL + viewModel.buildQueryFromThemeText(
                                        theme.text
                                    )
                                )
                            })
                        }
                    }

                    (viewModel.mediaDetails as? AnimeDetails)?.endingThemes?.let { themes ->
                        InfoTitle(text = stringResource(MR.strings.ending))
                        themes.forEach { theme ->
                            AnimeThemeItem(text = theme.text, onClick = {
                                context.openAction(
                                    Constants.YOUTUBE_QUERY_URL + viewModel.buildQueryFromThemeText(
                                        theme.text
                                    )
                                )
                            })
                        }
                    }
                }

                //Related
                if (viewModel.relatedAnime.isNotEmpty()) {
                    InfoTitle(text = stringResource(MR.strings.related_anime))
                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(viewModel.relatedAnime) { item ->
                            MediaItemVertical(
                                url = item.node.mainPicture?.large,
                                title = item.node.userPreferredTitle(),
                                modifier = Modifier.padding(end = 8.dp),
                                subtitle = {
                                    Text(
                                        text = item.relationLocalized(),
                                        color = MaterialTheme.colorScheme.outline,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    navigateToMediaDetails(MediaType.ANIME, item.node.id)
                                }
                            )
                        }
                    }
                }
                if (viewModel.relatedManga.isNotEmpty()) {
                    InfoTitle(text = stringResource(MR.strings.related_manga))
                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(viewModel.relatedManga) { item ->
                            MediaItemVertical(
                                url = item.node.mainPicture?.large,
                                title = item.node.userPreferredTitle(),
                                modifier = Modifier.padding(end = 8.dp),
                                subtitle = {
                                    Text(
                                        text = item.relationLocalized(),
                                        color = MaterialTheme.colorScheme.outline,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    navigateToMediaDetails(MediaType.MANGA, item.node.id)
                                }
                            )
                        }
                    }
                }

                //Recommendations
                if (viewModel.recommendations.isNotEmpty()) {
                    InfoTitle(text = stringResource(MR.strings.recommendations))
                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(viewModel.recommendations) { item ->
                            MediaItemVertical(
                                url = item.node.mainPicture?.large,
                                title = item.node.userPreferredTitle(),
                                modifier = Modifier.padding(end = 8.dp),
                                subtitle = {
                                    TextIconHorizontal(
                                        text = NumExtensions.numberFormat.format(item.numRecommendations),
                                        icon = R.drawable.ic_round_thumbs_up_down_16,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    navigateToMediaDetails(mediaType, item.node.id)
                                }
                            )
                        }
                    }
                }
            }//:Column
        }//:Scaffold
    }
}

@Composable
fun AnimeThemeItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}

@Composable
fun MediaInfoView(
    title: String,
    info: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .then(modifier)
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = info ?: stringResource(MR.strings.unknown),
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsTopAppBar(
    viewModel: MediaDetailsViewModel,
    mediaUrl: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateBack: () -> Unit,
    onClickNotification: (enable: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val savedForNotification = when (viewModel.mediaDetails?.status) {
        "currently_airing" -> remember {
            context.notificationsDataStore.data.map {
                it[stringPreferencesKey(viewModel.mediaDetails!!.id.toString())]
            }
        }.collectAsState(initial = null)

        "not_yet_aired" -> remember {
            context.notificationsDataStore.data.map {
                it[stringPreferencesKey("start_${viewModel.mediaDetails!!.id}")]
            }
        }.collectAsState(initial = null)

        else -> remember { mutableStateOf(null) }
    }

    TopAppBar(
        title = { Text(stringResource(MR.strings.title_details)) },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        actions = {
            if (viewModel.mediaDetails?.status == "currently_airing"
                || viewModel.mediaDetails?.status == "not_yet_aired"
            ) {
                IconButton(onClick = {
                    onClickNotification(savedForNotification.value == null)
                }) {
                    Icon(
                        painter = painterResource(
                            if (savedForNotification.value != null) R.drawable.round_notifications_active_24
                            else R.drawable.round_notifications_off_24
                        ),
                        contentDescription = "notification"
                    )
                }
            }
            ViewInBrowserButton(onClick = { context.openLink(mediaUrl) })

            ShareButton(url = mediaUrl)
        },
        scrollBehavior = scrollBehavior
    )
}

@Preview(showBackground = true)
@Composable
fun MediaDetailsPreview() {
    MoeListTheme {
        MediaDetailsView(
            mediaType = MediaType.ANIME,
            mediaId = 1,
            isLoggedIn = false,
            navigateBack = {},
            navigateToMediaDetails = { _, _ -> },
            navigateToFullPoster = {}
        )
    }
}