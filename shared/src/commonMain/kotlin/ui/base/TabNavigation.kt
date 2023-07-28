package ui.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab

interface SelectableTab : Tab {
    val iconSelected: Painter
        @Composable get
}

@Composable
fun RowScope.TabNavigationItem(
    tab: SelectableTab,
    onNavigate: () -> Unit = {}
) {
    val tabNavigator = LocalTabNavigator.current
    val isSelected = tabNavigator.current == tab

    NavigationBarItem(
        selected = isSelected,
        onClick = {
            onNavigate()
            tabNavigator.current = tab
        },
        icon = {
            Icon(
                painter = if (isSelected) tab.iconSelected else tab.options.icon!!,
                contentDescription = tab.options.title
            )
        },
        label = { Text(text = tab.options.title) },
    )
}

@Composable
fun Tab.TabContent(screen: Screen) {
    Navigator(screen)
}