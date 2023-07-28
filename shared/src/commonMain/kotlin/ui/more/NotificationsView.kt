package ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.moelist.common.MR
import data.datastore.PreferencesDataStore.getDataStore
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.composables.DefaultScaffoldWithTopAppBar

class NotificationsScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        val notifications = remember {
            getDataStore().data
        }.collectAsState(initial = null)

        DefaultScaffoldWithTopAppBar(
            title = stringResource(MR.strings.notifications),
            navigateBack = { navigator.pop() },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = {
                    coroutineScope.launch {
                        TODO()
                        //NotificationWorker.removeAllNotifications(context)
                    }
                }) {
                    Icon(
                        painter = painterResource("round_delete_sweep_24"),
                        contentDescription = stringResource(MR.strings.delete_all)
                    )
                    Text(
                        text = stringResource(MR.strings.delete_all),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(notifications.value?.asMap()?.keys?.toTypedArray() ?: emptyArray()) { key ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                TODO()
                                /*navigateToMediaDetails(
                                    MediaType.ANIME,
                                    key.toString().toInt()
                                )*/
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notifications.value?.get(key) as? String ?: "",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = {
                            coroutineScope.launch {
                                TODO()
                                /*NotificationWorker.removeAiringAnimeNotification(
                                    context = context,
                                    animeId = key.name.toIntOrNull() ?: 0
                                )*/
                            }
                        }) {
                            Icon(
                                painter = painterResource("delete_outline_24"),
                                contentDescription = stringResource(MR.strings.delete)
                            )
                        }
                    }
                }
            }//:LazyColumn
        }//:Scaffold
    }
}

/*
@Preview(showBackground = true)
@Composable
fun NotificationsPreview() {
    MoeListTheme {
        NotificationsView(
            navigateBack = {},
            navigateToMediaDetails = { _, _ -> },
        )
    }
}
*/