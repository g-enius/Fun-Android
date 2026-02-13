package com.funapp.android.platform.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.funapp.android.features.detail.DetailScreen
import com.funapp.android.features.profiledetail.ProfileDetailScreen
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import com.funapp.android.services.search.SearchService

@Composable
fun AppNavigation(
    networkService: NetworkService,
    favoritesService: FavoritesService,
    searchService: SearchService
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainTabScreen(
                networkService = networkService,
                favoritesService = favoritesService,
                searchService = searchService,
                onNavigateToDetail = { itemId ->
                    navController.navigate("detail/$itemId")
                },
                onNavigateToProfile = {
                    navController.navigate("profile/current_user")
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

        composable(
            route = "profile/{userId}",
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
