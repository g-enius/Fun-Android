package com.funapp.android.platform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.funapp.android.features.detail.DetailScreen
import com.funapp.android.features.login.LoginScreen
import com.funapp.android.features.profile.ProfileScreen
import com.funapp.android.features.profiledetail.ProfileDetailScreen
import com.funapp.android.model.DeepLink
import com.funapp.android.platform.ui.AppSettings
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import com.funapp.android.services.search.SearchService

@Composable
fun AppNavigation(
    networkService: NetworkService,
    favoritesService: FavoritesService,
    searchService: SearchService,
    appSettings: AppSettings,
    deepLink: DeepLink? = null,
    onDeepLinkHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(deepLink) {
        when (deepLink) {
            is DeepLink.SwitchTab -> {
                navController.popBackStack("main", inclusive = false)
                selectedTab = deepLink.tabIndex
                onDeepLinkHandled()
            }
            is DeepLink.ItemDetail -> {
                navController.popBackStack("main", inclusive = false)
                navController.navigate("detail/${deepLink.itemId}")
                onDeepLinkHandled()
            }
            is DeepLink.Profile -> {
                navController.popBackStack("main", inclusive = false)
                navController.navigate("profile")
                onDeepLinkHandled()
            }
            null -> {}
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainTabScreen(
                networkService = networkService,
                favoritesService = favoritesService,
                searchService = searchService,
                appSettings = appSettings,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToDetail = { itemId ->
                    navController.navigate("detail/$itemId")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        composable(
            route = "detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            DetailScreen(
                itemId = itemId,
                networkService = networkService,
                favoritesService = favoritesService,
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                networkService = networkService,
                onSearchItems = {
                    selectedTab = 1
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "profile-detail/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            ProfileDetailScreen(
                userId = userId,
                networkService = networkService,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
