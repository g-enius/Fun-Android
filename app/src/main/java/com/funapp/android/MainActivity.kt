package com.funapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.funapp.android.platform.navigation.AppNavigation
import com.funapp.android.platform.ui.theme.FunTheme
import com.funapp.android.services.favorites.DefaultFavoritesService
import com.funapp.android.services.network.DefaultNetworkService
import com.funapp.android.services.search.DefaultSearchService

class MainActivity : ComponentActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences("fun_prefs", MODE_PRIVATE)
    }
    private val networkService by lazy { DefaultNetworkService() }
    private val favoritesService by lazy { DefaultFavoritesService(sharedPreferences) }
    private val searchService by lazy { DefaultSearchService(networkService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunTheme {
                AppNavigation(
                    networkService = networkService,
                    favoritesService = favoritesService,
                    searchService = searchService
                )
            }
        }
    }
}
