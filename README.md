# Fun - Android Demo App

[![CI](https://github.com/g-enius/Fun-Android/actions/workflows/ci.yml/badge.svg)](https://github.com/g-enius/Fun-Android/actions/workflows/ci.yml)

A modern Android application demonstrating clean architecture (MVVM), Jetpack Compose, multi-module design, and best practices for scalable Android development. Android counterpart of the [Fun iOS app](https://github.com/g-enius/Fun-iOS).

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.1.10 |
| UI Framework | Jetpack Compose (Material 3) |
| Reactive & Concurrency | Kotlin Coroutines, StateFlow |
| Architecture | MVVM + Navigation Compose |
| Dependency Injection | Manual (ViewModelFactory) |
| Build System | Gradle 8.9, Version Catalog |
| Minimum SDK | API 26 (Android 8.0) |
| Target SDK | API 35 (Android 15) |
| Testing | JUnit 5, MockK, Turbine |

## Module Structure

```
Fun-Android/
├── app/                        # Application entry point, DI wiring
├── model/                      # Data models (Item, UserProfile, etc.)
├── platform/
│   ├── ui-components/          # Theme, shared composables, ViewModelFactory
│   └── navigation/             # NavGraph, bottom tab navigation
├── services/
│   ├── network/                # Network service interface + mock implementation
│   ├── favorites/              # Favorites service (SharedPreferences-backed)
│   └── search/                 # Search service (delegates to network)
└── features/
    ├── home/                   # Home tab (item list + favorites)
    ├── search/                 # Search tab (debounced query)
    ├── items/                  # Favorites tab (filtered items)
    ├── profile/                # Profile tab (current user)
    ├── profile-detail/         # Profile detail screen
    ├── detail/                 # Item detail screen
    └── settings/               # Settings tab (feature flags)
```

14 modules total. All modules except `app` are Android library modules.

**Dependency Hierarchy:**
```
app → navigation → features/* → services/* → model
         └──────→ ui-components ──────────────┘
```

## Key Patterns

### MVVM + Navigation Compose
- **ViewModel**: Business logic, StateFlow-based state management
- **Screen**: Composable that creates ViewModel, collects state
- **Content**: Pure UI composable (stateless, previewable)
- **Navigation**: Single NavHost with bottom tab screen + detail routes

### Manual Dependency Injection

Services are created lazily in `MainActivity` and passed through the composable tree via function parameters. No framework (Hilt/Koin/Dagger) needed for a demo app.

```kotlin
// MainActivity.kt
private val networkService by lazy { DefaultNetworkService() }
private val favoritesService by lazy { DefaultFavoritesService(sharedPreferences) }
private val searchService by lazy { DefaultSearchService(networkService) }
```

### Screen/Content Split

Every feature follows the same pattern for testability and preview support:

```kotlin
// Screen.kt - ViewModel creation + state collection
@Composable
fun HomeScreen(networkService: NetworkService, ...) {
    val viewModel: HomeViewModel = viewModel(factory = viewModelFactory { ... })
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(state = state, onRefresh = viewModel::onRefresh, ...)
}

// Content.kt - Pure UI, no ViewModel dependency
@Composable
internal fun HomeContent(state: HomeState, onRefresh: () -> Unit, ...) { ... }
```

### Atomic State Updates

All ViewModels use `MutableStateFlow.update {}` for thread-safe, atomic state mutations:

```kotlin
_state.update { current ->
    current.copy(items = current.items.map { ... })
}
```

## Features

- **5-Tab Navigation**: Home, Search, Favorites, Profile, Settings
- **Detail Screens**: Item detail with favorite toggle, profile detail with back navigation
- **Reactive Favorites**: SharedPreferences-backed, observed via StateFlow across all screens
- **Debounced Search**: 300ms debounce with `distinctUntilChanged` and `flatMapLatest`
- **Feature Flags**: Runtime toggles in Settings (UI-only for demo)
- **Material 3 Theming**: Dynamic color support, light/dark mode
- **Error Handling**: Loading/error/content states with retry support
- **Pull-to-Refresh**: On applicable screens

## Navigation

```
NavHost
├── "main" → MainTabScreen (5 tabs)
│   ├── Tab 0: HomeScreen
│   ├── Tab 1: SearchScreen
│   ├── Tab 2: ItemsScreen (Favorites)
│   ├── Tab 3: ProfileScreen
│   └── Tab 4: SettingsScreen
├── "detail/{itemId}" → DetailScreen
└── "profile/{userId}" → ProfileDetailScreen
```

## Testing

- **Unit Tests**: JUnit 5 + MockK + Coroutines Test
- **12 tests** across 3 ViewModels (Home, Detail, Settings)
- **Coroutine testing**: `StandardTestDispatcher` + `advanceUntilIdle`

```bash
./gradlew test
```

## Getting Started

### Requirements
- Android Studio Ladybug (2025.3.1+)
- JDK 17+
- Android SDK 35

### Installation
```bash
git clone https://github.com/g-enius/Fun-Android.git
cd Fun-Android
```

### Running
1. Open project in Android Studio
2. Sync Gradle
3. Select `app` configuration
4. Choose emulator or device
5. Run (Shift + F10)

### Build from Command Line
```bash
./gradlew assembleDebug
./gradlew installDebug    # install on connected device/emulator
./gradlew test            # run unit tests
```

## AI-Assisted Development

This project demonstrates **AI-assisted Android development** using Claude Code.

Architecture and patterns designed by developer. Claude assisted with:
- Full project scaffolding (14 modules)
- Feature implementation
- Code review (5-agent parallel review)
- Bug fixes (atomic StateFlow, nav safety, padding)
- Test coverage

Commits with AI assistance include `Co-Authored-By: Claude` attribution.

---

MIT License
