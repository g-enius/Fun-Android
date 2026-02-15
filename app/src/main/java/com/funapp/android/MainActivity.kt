package com.funapp.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.funapp.android.model.DeepLink
import com.funapp.android.platform.navigation.AppNavigation
import com.funapp.android.platform.ui.AppSettings
import com.funapp.android.platform.ui.theme.FunTheme
import com.funapp.android.services.favorites.DefaultFavoritesService
import com.funapp.android.services.network.DefaultNetworkService
import com.funapp.android.services.ai.DefaultAiService
import com.funapp.android.services.search.DefaultSearchService

class MainActivity : ComponentActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences("fun_prefs", MODE_PRIVATE)
    }
    private val appSettings by lazy { AppSettings() }
    private val networkService by lazy { DefaultNetworkService(simulateErrors = appSettings.simulateErrorsEnabled) }
    private val favoritesService by lazy { DefaultFavoritesService(sharedPreferences) }
    private val searchService by lazy { DefaultSearchService(networkService) }
    private val aiService by lazy { DefaultAiService(applicationContext) }

    private var pendingDeepLink by mutableStateOf<DeepLink?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingDeepLink = DeepLink.parse(intent?.data?.toString())
        setContent {
            val appearanceMode by appSettings.appearanceMode.collectAsStateWithLifecycle()
            FunTheme(appearanceMode = appearanceMode) {
                AppNavigation(
                    networkService = networkService,
                    favoritesService = favoritesService,
                    searchService = searchService,
                    aiService = aiService,
                    appSettings = appSettings,
                    deepLink = pendingDeepLink,
                    onDeepLinkHandled = { pendingDeepLink = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingDeepLink = DeepLink.parse(intent.data?.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        aiService.close()
    }
}
