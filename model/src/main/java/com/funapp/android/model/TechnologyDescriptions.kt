package com.funapp.android.model

object TechnologyDescriptions {

    fun description(itemId: String): String? = descriptions[itemId]

    private val descriptions = mapOf(
        "1" to """
Kotlin Coroutines provide a lightweight concurrency framework for Android development:

• Suspend functions let you write asynchronous code sequentially
• Structured concurrency ensures child coroutines are cancelled when their scope ends
• StateFlow and SharedFlow provide observable state for your UI layer
• Integration with Jetpack libraries like ViewModel, Room, and Retrofit

Example from HomeViewModel:
```kotlin
suspend fun loadFeaturedItems() {
    delay(500) // Simulate network latency
    val items = networkService.fetchHomeData()
    _state.update { currentState ->
        currentState.copy(
            isLoading = false,
            items = items.getOrDefault(emptyList())
        )
    }
}
```
""".trimIndent(),

        "2" to """
Kotlin Flow is a reactive streams library built on coroutines that provides cold asynchronous data streams:

• Cold streams that only emit when collected
• Operators like map, filter, debounce, and flatMapLatest for transforming data
• StateFlow for observable state and SharedFlow for events
• Seamless integration with Jetpack Compose via collectAsStateWithLifecycle

Example from SearchViewModel:
```kotlin
searchQuery
    .debounce(400)
    .filter { it.length >= 2 }
    .flatMapLatest { query ->
        flow {
            emit(SearchState(isSearching = true))
            val results = networkService.searchItems(query)
            emit(SearchState(results = results.getOrDefault(emptyList())))
        }
    }
    .collect { state -> _state.value = state }
```
""".trimIndent(),

        "3" to """
Jetpack Compose is Android's modern declarative UI toolkit that simplifies and accelerates UI development:

• Declarative API where UI is a function of state
• State hoisting pattern separates state management from UI rendering
• Recomposition efficiently updates only changed parts of the UI tree
• Material 3 design system with built-in theming and components

Example from HomeScreen:
```kotlin
@Composable
fun HomeScreen(
    state: HomeState,
    onItemClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()
    Scaffold(topBar = { TopAppBar(title = { Text("Home") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state.items) { item ->
                ItemCard(item = item, onClick = { onItemClick(item.id) })
            }
        }
    }
}
```
""".trimIndent(),

        "4" to """
Jetpack Navigation manages app navigation with type-safe arguments and back stack management:

• NavHost defines the navigation graph with composable destinations
• Type-safe arguments pass data between screens
• Deep link support for external navigation into the app
• Back stack management with popBackStack and saveState

Example from NavGraph:
```kotlin
NavHost(navController = navController, startDestination = "main") {
    composable("main") {
        MainTabScreen(onNavigateToDetail = { itemId ->
            navController.navigate("detail/${'$'}itemId")
        })
    }
    composable(
        route = "detail/{itemId}",
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId")
        DetailScreen(itemId = itemId ?: return@composable)
    }
}
```
""".trimIndent(),

        "5" to """
Model-View-ViewModel separates UI logic from business logic for testable, maintainable code:

• ViewModel survives configuration changes and manages UI state
• StateFlow exposes observable state that Compose collects
• Unidirectional data flow: events go up, state flows down
• Business logic stays in the ViewModel, UI only renders state

Example from DetailViewModel:
```kotlin
class DetailViewModel(
    private val itemId: String,
    private val networkService: NetworkService
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState(isLoading = true))
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init { loadDetails() }

    private fun loadDetails() {
        viewModelScope.launch {
            networkService.fetchItemDetails(itemId)
                .onSuccess { item ->
                    _state.update { DetailState(item = item) }
                }
        }
    }
}

// In Compose:
val state by viewModel.state.collectAsStateWithLifecycle()
```
""".trimIndent(),

        "6" to """
Gradle multi-module architecture splits your app into focused modules with clear boundaries:

• Each module has explicit dependencies and can be built independently
• Faster incremental builds since only changed modules recompile
• Enforces separation of concerns at the build system level
• Feature modules depend on shared modules but not on each other

Example module dependency tree:
```kotlin
// :app module - depends on feature and platform modules
implementation(project(":features:home"))
implementation(project(":features:detail"))
implementation(project(":features:items"))
implementation(project(":platform:navigation"))
implementation(project(":platform:ui"))

// :features:home - depends on shared modules only
implementation(project(":model"))
implementation(project(":services:network"))
implementation(project(":platform:ui"))

// :model - no internal dependencies (leaf module)
```
""".trimIndent(),

        "7" to """
Manual dependency injection passes dependencies through constructors without any framework:

• No annotation processing or code generation — just plain Kotlin constructors
• Dependencies are created in MainActivity and threaded through navigation
• Interfaces define contracts, concrete types are only known at the composition root
• Full control over object lifetimes with lazy initialization

Example from this app:
```kotlin
// MainActivity creates services at the composition root
class MainActivity : ComponentActivity() {
    private val appSettings by lazy { AppSettings() }
    private val networkService by lazy {
        DefaultNetworkService(simulateErrors = appSettings.simulateErrorsEnabled)
    }
    private val favoritesService by lazy { DefaultFavoritesService() }
}

// ViewModels receive dependencies through their constructor
class DetailViewModel(
    private val itemId: String,
    private val networkService: NetworkService,
    private val favoritesService: FavoritesService
) : ViewModel()
```
""".trimIndent(),

        "8" to """
Interface-oriented design in Kotlin favors interfaces over inheritance for flexible, testable code:

• Define behavior through interfaces, provide concrete implementations
• Swap implementations for testing with mock or fake versions
• Depends on abstractions, not concrete types
• Kotlin interfaces support default method implementations

Example from NetworkService:
```kotlin
// Interface defines the contract
interface NetworkService {
    suspend fun fetchHomeData(): Result<List<Item>>
    suspend fun fetchItemDetails(itemId: String): Result<Item>
    suspend fun searchItems(query: String): Result<SearchResult>
}

// Production implementation
class DefaultNetworkService : NetworkService {
    override suspend fun fetchHomeData(): Result<List<Item>> {
        return withContext(Dispatchers.IO) { /* real fetch */ }
    }
}

// Test mock
class MockNetworkService(
    var itemsToReturn: List<Item> = emptyList()
) : NetworkService {
    override suspend fun fetchHomeData() = Result.success(itemsToReturn)
}
```
""".trimIndent(),

        "9" to """
Feature toggles allow you to enable or disable features at runtime without redeployment:

• A/B testing by enabling features for a subset of users
• Gradual rollouts with percentage-based activation
• Quick kill switches to disable problematic features
• Runtime configuration via StateFlow for reactive UI updates

Example from AppSettings:
```kotlin
class AppSettings {
    private val _carouselEnabled = MutableStateFlow(true)
    val carouselEnabled: StateFlow<Boolean> = _carouselEnabled.asStateFlow()

    fun toggleCarousel() {
        _carouselEnabled.update { !it }
    }
}

// Observing in a ViewModel:
appSettings.carouselEnabled.collect { enabled ->
    _state.update { it.copy(showCarousel = enabled) }
}

// Reacting in Compose:
val carouselEnabled by appSettings.carouselEnabled.collectAsStateWithLifecycle()
if (carouselEnabled) {
    FeaturedCarousel(items = state.featuredItems)
}
```
""".trimIndent(),

        "10" to """
Timber is a popular Android logging library that extends the standard Log API:

• Automatic tag generation from the calling class name
• Tree-based architecture lets you plant different loggers for debug and release
• DebugTree for development, custom trees for crash reporting in production
• Drop-in replacement for android.util.Log with less boilerplate

Example from Application setup:
```kotlin
// Plant in Application.onCreate()
class FunApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }
}

// Usage throughout the app:
Timber.d("Loading items for category: %s", category)
Timber.e(exception, "Failed to fetch item details")
Timber.i("User toggled favorite for item %s", itemId)
```
""".trimIndent(),

        "11" to """
Kotlin 2.0 brings the new K2 compiler with dramatically faster compilation and improved type inference:

• Up to 2x faster compilation with the new frontend
• Improved type inference reduces need for explicit type annotations
• Better IDE support with faster code analysis
• Foundation for future language features like context receivers

Example K2 compiler configuration in build.gradle.kts:
```kotlin
// root build.gradle.kts
plugins {
    kotlin("android") version "2.0.0" apply false
}

// module build.gradle.kts
kotlin {
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.addAll(
            "-Xcontext-receivers",
            "-Xexpect-actual-classes"
        )
    }
}
```
""".trimIndent(),

        "12" to """
JUnit 5 is the modern Java/Kotlin testing framework with expressive APIs for comprehensive testing:

• @Test annotation marks test methods
• @Nested classes organize related tests into groups
• Rich assertion library with assertAll, assertThrows, and more
• Parameterized tests reduce duplication for similar test cases

Example from DetailViewModelTest:
```kotlin
class DetailViewModelTest {

    @Test
    fun `loadDetails sets item on success`() {
        val mockService = MockNetworkService(
            itemsToReturn = listOf(testItem)
        )
        val viewModel = DetailViewModel("1", mockService)

        assertEquals(testItem, viewModel.state.value.item)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Nested
    inner class FavoriteToggle {
        @Test
        fun `toggle adds to favorites when not favorited`() {
            viewModel.onFavoriteToggle()
            assertTrue(favoritesService.isFavorite("1"))
        }

        @Test
        fun `toggle removes from favorites when already favorited`() {
            favoritesService.addFavorite("1")
            viewModel.onFavoriteToggle()
            assertFalse(favoritesService.isFavorite("1"))
        }
    }
}
```
""".trimIndent(),

        "13" to """
Screenshot testing captures rendered Compose UI and compares against reference images:

• Catches unintended visual regressions automatically
• Runs during CI to prevent visual bugs from merging
• Compose test rule renders components in isolation
• Reference images are stored in version control for comparison

Example from a screenshot test:
```kotlin
class ItemCardScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun itemCard_defaultState() {
        composeTestRule.setContent {
            FunTheme {
                ItemCard(
                    item = Item(
                        id = "1",
                        title = "Coroutines",
                        subtitle = "Lightweight concurrency",
                        category = "Concurrency"
                    ),
                    onClick = {}
                )
            }
        }
        composeTestRule
            .onRoot()
            .captureToImage()
            .assertAgainstGolden("item_card_default")
    }
}
```
""".trimIndent(),

        "14" to """
Building accessible Android apps ensures everyone can use your software:

• TalkBack reads screen content aloud for visually impaired users
• Content descriptions provide meaningful labels for UI elements
• Semantic properties define roles, states, and actions for assistive technology
• Touch target sizing ensures elements are easy to interact with

Example from Compose accessibility:
```kotlin
@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "${'$'}{item.title}: ${'$'}{item.subtitle}"
                role = Role.Button
            }
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = null, // Decorative, parent has description
            modifier = Modifier.size(40.dp)
        )
        Column {
            Text(text = item.title)
            Text(
                text = item.subtitle,
                modifier = Modifier.clearAndSetSemantics { }
            )
        }
    }
}
```
""".trimIndent()
    )
}
