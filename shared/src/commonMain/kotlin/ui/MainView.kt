package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import data.datastore.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.LAST_TAB_PREFERENCE_KEY
import data.datastore.PreferencesDataStore.rememberPreference
import kotlinx.coroutines.launch
import ui.base.TabNavigationItem
import ui.home.HomeTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    lastTabOpened: Int
) {
    val density = LocalDensity.current
    val accessTokenPreference by rememberPreference(ACCESS_TOKEN_PREFERENCE_KEY, "")
    var selectedTab by rememberPreference(LAST_TAB_PREFERENCE_KEY, lastTabOpened)
    val scope = rememberCoroutineScope()

    val bottomBarState = remember { mutableStateOf(true) }
    var topBarHeightPx by remember { mutableStateOf(0f) }
    val topBarOffsetY = remember { Animatable(0f) }
    var scaffoldPadding = remember { PaddingValues() }

    TabNavigator(
        HomeTab(
            isLoggedIn = accessTokenPreference.isNotEmpty(),
            topBarHeightPx = topBarHeightPx,
            topBarOffsetY = topBarOffsetY,
            padding = scaffoldPadding
        )
    ) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    bottomBarState = bottomBarState,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = topBarOffsetY.value
                        }
                )
            },
            bottomBar = {
                BottomNavBar(
                    bottomBarState = bottomBarState,
                    lastTabOpened = lastTabOpened,
                    topBarOffsetY = topBarOffsetY,
                ) {
                    TabNavigationItem(
                        tab = HomeTab(
                            isLoggedIn = accessTokenPreference.isNotEmpty(),
                            topBarHeightPx = topBarHeightPx,
                            topBarOffsetY = topBarOffsetY,
                            padding = scaffoldPadding
                        ),
                        onNavigate = {
                            scope.launch {
                                topBarOffsetY.animateTo(0f)
                            }
                            selectedTab = 0
                        }
                    )
                }
            },
            contentWindowInsets = WindowInsets.systemBars
                .only(WindowInsetsSides.Horizontal)
        ) { padding ->
            LaunchedEffect(padding) {
                scaffoldPadding = padding
                topBarHeightPx = density.run { padding.calculateTopPadding().toPx() }
            }

            CurrentTab()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    bottomBarState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val isVisible = remember {
        true
        /*derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                HOME_DESTINATION, ANIME_LIST_DESTINATION, MANGA_LIST_DESTINATION, MORE_DESTINATION,
                null -> true

                else -> false
            }
        }*/
    }
    var query by remember { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(false) }
    var active by remember { mutableStateOf(false) }
    //val profilePictureUrl by rememberPreference(PROFILE_PICTURE_PREFERENCE_KEY, "")

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (!active) Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                    else Modifier
                )
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*SearchBar(
                query = query,
                onQueryChange = {
                    query = it
                },
                onSearch = {
                    performSearch.value = true
                },
                active = active,
                onActiveChange = {
                    bottomBarState.value = !it
                    active = it
                    if (!active) query = ""
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(MR.strings.search)) },
                leadingIcon = {
                    if (active) {
                        BackIconButton(
                            onClick = {
                                active = false
                                bottomBarState.value = true
                                query = ""
                            }
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_round_search_24),
                            contentDescription = "search"
                        )
                    }
                },
                trailingIcon = {
                    if (!active) {
                        AsyncImage(
                            model = profilePictureUrl,
                            contentDescription = "profile",
                            placeholder = painterResource(R.drawable.ic_round_account_circle_24),
                            error = painterResource(R.drawable.ic_round_account_circle_24),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(100))
                                .size(32.dp)
                                .clickable { navController.navigate(PROFILE_DESTINATION) }
                        )
                    } else if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "delete"
                            )
                        }
                    }
                }
            ) {
                SearchView(
                    query = query,
                    performSearch = performSearch,
                    navigateToMediaDetails = { mediaType, mediaId ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace("{mediaType}", mediaType.name)
                                .replace("{mediaId}", mediaId.toString())
                        )
                    }
                )
            }//:SearchBar
            */
        }//:Column
    }
}

@Composable
fun BottomNavBar(
    bottomBarState: State<Boolean>,
    lastTabOpened: Int,
    topBarOffsetY: Animatable<Float, AnimationVector1D>,
    content: @Composable (RowScope.() -> Unit)
) {
    val isVisible = remember {
        true
        /*derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                HOME_DESTINATION, ANIME_LIST_DESTINATION, MANGA_LIST_DESTINATION, MORE_DESTINATION,
                null -> bottomBarState.value

                else -> false
            }
        }*/
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar(content = content)
    }
}

/*
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MoeListTheme {
        MainView(
            navController = rememberNavController(),
            lastTabOpened = 0
        )
    }
}
*/
