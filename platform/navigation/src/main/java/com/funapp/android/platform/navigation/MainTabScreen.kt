package com.funapp.android.platform.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.funapp.android.features.home.HomeScreen
import com.funapp.android.features.items.ItemsScreen
import com.funapp.android.features.profile.ProfileScreen
import com.funapp.android.features.search.SearchScreen
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
    onNavigateToProfile: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

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
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> HomeScreen(
                networkService = networkService,
                favoritesService = favoritesService,
                onNavigateToDetail = onNavigateToDetail
            )
            1 -> SearchScreen(
                searchService = searchService,
                favoritesService = favoritesService,
                onNavigateToDetail = onNavigateToDetail
            )
            2 -> ItemsScreen(
                networkService = networkService,
                favoritesService = favoritesService,
                onNavigateToDetail = onNavigateToDetail
            )
            3 -> ProfileScreen(
                networkService = networkService,
                onNavigateToProfile = onNavigateToProfile,
                onViewProfile = { onNavigateToProfile("current_user") }
            )
            4 -> SettingsScreen()
        }
    }
}
