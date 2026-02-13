package com.funapp.android.platform.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.funapp.android.features.home.HomeScreen
import com.funapp.android.features.items.ItemsScreen
import com.funapp.android.features.settings.SettingsScreen
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import com.funapp.android.services.search.SearchService

@Composable
fun MainTabScreen(
    networkService: NetworkService,
    favoritesService: FavoritesService,
    searchService: SearchService,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Items") },
                    label = { Text("Items") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    networkService = networkService,
                    favoritesService = favoritesService,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToProfile = onNavigateToProfile
                )
                1 -> ItemsScreen(
                    networkService = networkService,
                    favoritesService = favoritesService,
                    onNavigateToDetail = onNavigateToDetail
                )
                2 -> SettingsScreen()
            }
        }
    }
}
