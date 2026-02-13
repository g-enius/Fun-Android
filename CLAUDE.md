# Fun-Android

## Build & Test
- 14 modules: app + model + platform(ui-components, navigation) + services + features
- Package: `com.funapp.android` (NOT com.fun — 'fun' is Kotlin reserved)
- Build: `./gradlew assembleDebug`
- Unit tests: `./gradlew test`
- Connected tests: `./gradlew connectedAndroidTest`
- Use replicant MCP tools (gradle-build, gradle-test, adb-*) over raw CLI commands

## Code Style
- Kotlin, Jetpack Compose, MVVM + Navigation Compose
- Manual DI — no Hilt, Dagger, or Koin
- StateFlow + Coroutines for reactive state
- No `collectAsState` — use `WithLifecycle` pattern
- No GlobalScope — use structured concurrency

## Testing
- JUnit5 + MockK (NOT Mockito) — use `every{}` / `coEvery{}`, not `whenever()`
- Paparazzi for snapshot tests
- 75%+ coverage target
