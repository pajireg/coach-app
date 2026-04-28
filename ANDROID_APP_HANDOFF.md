# Android App Handoff

This document is the Android-specific implementation handoff. Product scope,
backend API contract, and cross-platform boundaries are defined in
`MOBILE_APP_HANDOFF.md` — read that first and treat it as the source of truth
for what the backend exposes. Visual direction comes from the design bundle
at `design/running-coach/` — read that next. This document defines *how* the
Android app is built so the two align.

An iOS implementation exists at \`ios/\` and is complete through milestone 1.
Reference it for backend response shapes and shared design decisions, but do
not mirror its internal structure — use idiomatic Android patterns instead.

## Design Source & Fidelity

The design bundle at `design/running-coach/` was authored as a web prototype.
Primary files:

- `design/running-coach/README.md` — handoff instructions.
- `design/running-coach/chats/chat1.md` — full design conversation. Contains
  stated direction: warm off-white/navy palette, Pretendard + JetBrains Mono,
  zone-coded accents, floating tab bar pill, large → collapsed nav on scroll.
- `design/running-coach/project/Running Coach.html` — primary prototype.
- `design/running-coach/project/src/tokens.jsx` — authoritative color/type/
  zone values.
- `design/running-coach/project/src/primitives.jsx` — component geometry
  (tab bar, nav bar, cards, buttons, screen scaffold).
- `design/running-coach/project/src/{home,weekly,trends,goals,settings,
  workout,feedback,onboarding}.jsx` — per-screen layouts.

Implementation rule: match the **visual output** (colors, spacing, type,
geometry). The prototype is web/JSX — translate to idiomatic Jetpack Compose,
not a line-for-line port.

The design was originally conceived for iOS 26 Liquid Glass. On Android, the
closest equivalents are Material 3 elevated surfaces with custom translucent
backgrounds using `RenderEffect` blur (API 31+) or a tinted opaque fallback
for API 26–30. Preserve the translucency feel on Android 12+ devices; let
it degrade gracefully to opaque elevated cards on older APIs.

## Target & Audience

- Same primary users as iOS: Korean 20–30s running crews using Garmin devices.
- Garmin handles workout execution. The Android app is a coaching companion.
- **Wear OS support is out of scope for milestone 1.** Do not add a Wear OS
  module yet.

## Stack & Versions

- Language: **Kotlin** (latest stable, 2.x+). No Java.
- UI: **Jetpack Compose** (BOM latest stable). No View system / XML layouts.
- State: **ViewModel + StateFlow** (Kotlin Coroutines + Flow). No LiveData.
- Concurrency: **Kotlin Coroutines (`suspend` / `Flow`)**. No RxJava.
- DI: **Hilt** (Dagger Hilt).
- Networking: **Retrofit 2 + OkHttp 4**. Kotlin serialization (kotlinx)
  for JSON.
- Minimum SDK: **API 26 (Android 8.0)** — covers ~97% of active devices.
- Target/Compile SDK: **API 35 (Android 15)**.
- Android Studio: latest stable (Ladybug or newer).

Key Gradle dependencies (add exact versions via BOM / version catalog):

```kotlin
// BOM
platform("androidx.compose:compose-bom:latest")

// Compose
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.compose.ui:ui-tooling-preview
androidx.activity:activity-compose
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.navigation:navigation-compose

// DI
com.google.dagger:hilt-android
androidx.hilt:hilt-navigation-compose

// Networking
com.squareup.retrofit2:retrofit
com.squareup.okhttp3:okhttp
com.squareup.okhttp3:logging-interceptor
org.jetbrains.kotlinx:kotlinx-serialization-json
com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter

// Secure storage
androidx.security:security-crypto   // EncryptedSharedPreferences

// Testing
junit:junit
io.mockk:mockk
kotlinx-coroutines-test
```

## Design System

Centralize all tokens in `design/Theme.kt`, `design/Color.kt`, and
`design/Type.kt`. Do not scatter hex literals or dimension values in screens.

### Color Palette

Implement as a Kotlin `data class RcColors` passed through
`CompositionLocalProvider`. Both light and dark instances are needed.

```
LIGHT THEME
  bg              #F2EFE8  (warm off-white)
  bgElev          #FFFFFF
  bgElev2         #FAF8F3
  text            #0B1220
  textDim         #0B1220 @ 68% alpha
  textFaint       #0B1220 @ 46% alpha
  textMuted       #0B1220 @ 30% alpha
  border          #0B1220 @ 8% alpha
  borderStrong    #0B1220 @ 16% alpha

DARK THEME
  bg              #0B1220  (deep navy)
  bgElev          #121A2B
  bgElev2         #1A2236
  text            #F5F3EE
  textDim         #F5F3EE @ 72% alpha
  textFaint       #F5F3EE @ 50% alpha
  textMuted       #F5F3EE @ 32% alpha
  border          #FFFFFF @ 10% alpha
  borderStrong    #FFFFFF @ 18% alpha
```

Register `RcColors` as a `CompositionLocal`:

```kotlin
val LocalRcColors = compositionLocalOf { LightRcColors }
val rcColors @Composable get() = LocalRcColors.current
```

### Zone Accents

```kotlin
object ZoneColors {
    val recovery  = Color(0xFF4A9EDB)
    val base      = Color(0xFF3FB87F)
    val threshold = Color(0xFFE8A94C)
    val interval  = Color(0xFFE35D5D)
    val rest      = Color(0xFF8A8F99)
    val long      = Color(0xFF8F7BD4)
}

enum class Zone { RECOVERY, BASE, THRESHOLD, INTERVAL, REST, LONG }

fun Zone.color() = when(this) { ... }
fun Zone.label() = when(this) { Zone.RECOVERY -> "회복"; ... }
fun String.toZone() = when(lowercase()) {
    "recovery" -> Zone.RECOVERY
    "base"     -> Zone.BASE
    ...
    else       -> Zone.REST
}
```

Soft tint backgrounds use the accent color at 14–16% alpha.

### Typography

- UI font: **Pretendard Variable**. Ship `PretendardVariable.ttf` in
  `res/font/`. Load via `FontFamily(Font(R.font.pretendard_variable))`.
- Data font: **JetBrains Mono**. Ship
  `JetBrainsMono-VariableFont_wght.ttf` in `res/font/`. Use for all
  numeric/pace/distance/HR displays.
- Enable `fontFeatureSettings = "tnum"` on all JetBrains Mono `Text` and
  on numeric Pretendard fields for tabular figures.

```kotlin
val PretendardFamily = FontFamily(Font(R.font.pretendard_variable))
val MonoFamily       = FontFamily(Font(R.font.jetbrains_mono_variable))

val RcTypography = Typography(
    displayLarge  = TextStyle(fontFamily = PretendardFamily, fontSize = 32.sp,
                              fontWeight = FontWeight.Bold, letterSpacing = (-0.8).sp),
    titleMedium   = TextStyle(fontFamily = PretendardFamily, fontSize = 17.sp,
                              fontWeight = FontWeight.SemiBold, letterSpacing = (-0.3).sp),
    bodyMedium    = TextStyle(fontFamily = PretendardFamily, fontSize = 15.sp),
    labelSmall    = TextStyle(fontFamily = PretendardFamily, fontSize = 11.sp,
                              fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp)
)
// eyebrow: labelSmall in uppercase
// metric:  MonoFamily, 40–56sp, weight 500–600
```

### Spacing & Dimensions

```kotlin
object Spacing {
    val xs = 4.dp;  val sm = 8.dp;  val md = 16.dp
    val lg = 20.dp; val xl = 24.dp; val xxl = 32.dp
    val screenHorizontal = 20.dp
    val bottomContentPad = 120.dp  // clears floating tab bar
}

object Radius {
    val sm = 18.dp; val md = 22.dp; val lg = 28.dp
    val hero = 34.dp; val pill = 100.dp; val tabBar = 30.dp
}
```

### Translucent / Glass Surface

On API 31+, apply blur via `Modifier.blur()` (Compose 1.4+) or
`RenderEffect`. Below API 31, fall back to the solid `bgElev` color.

```kotlin
@Composable
fun Modifier.rcGlassBackground(
    alpha: Float = 0.62f,
    cornerRadius: Dp = Radius.md,
    isCard: Boolean = true,
): Modifier {
    val colors = rcColors
    return this.then(
        if (Build.VERSION.SDK_INT >= 31) {
            Modifier
                .background(
                    color = (if (colors.isDark) Color(0xFF1C263C) else Color.White).copy(alpha = alpha),
                    shape = RoundedCornerShape(cornerRadius)
                )
                // blur via BlurMaskFilter in drawWithContent or windowBlur
        } else {
            Modifier.background(
                color = (if (colors.isDark) colors.bgElev else colors.bgElev).copy(alpha = 0.92f),
                shape = RoundedCornerShape(cornerRadius)
            )
        }
    )
}
```

Card hairline: a 1dp horizontal gradient stroke (transparent → white/0.9 →
transparent) at the top inside edge of each card.

### Ambient Background Wash

Three soft radial ellipses below all content, matching the iOS approach:

```kotlin
@Composable
fun AmbientBackground() {
    Box(Modifier.fillMaxSize()) {
        // top-left: recovery blue @ 12–14%
        // top-right: threshold amber @ 9–12%
        // bottom-center: long purple @ 10–12%
        // Implemented as Canvas radial draws with BlendMode.SrcOver
    }
}
```

### Screen Scaffold

Every tab screen uses `RcScreen`:

```kotlin
@Composable
fun RcScreen(
    title: String,
    eyebrow: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable LazyListScope.() -> Unit,
)
```

Behaviour:
- `LazyColumn` root. First items: eyebrow + large title (32sp, in-scroll,
  not pinned).
- Track `LazyListState.firstVisibleItemScrollOffset`; when scrollY > 28dp,
  animate the glass nav bar in from the top with the compact title.
- Bottom content padding: 120dp to clear the floating tab bar.
- Background: `AmbientBackground` + theme bg color, edge-to-edge
  (`WindowCompat.setDecorFitsSystemWindows(window, false)`).

### Component Library (match primitives.jsx)

Build as a `design/components/` package:

- `RcTabBar` — floating glass pill, 5 tabs, active item tint.
- `RcNavBar` — appears on scroll, glass background with compact title.
- `RcCard` — translucent surface card, optional 3dp leading accent strip.
- `RcButton` — primary (filled, zone/accent), secondary (outlined glass),
  ghost (text only).
- `RcZoneBadge` — zone chip (filled or soft tint).
- `RcSectionHeader` — 11sp uppercase eyebrow + optional right action.
- `ScoreSlider` — integer 1–10 thumb, no value = unset state.

## Project Layout

```
coach-app/
  android/
    app/
      src/
        main/
          java/com/suminchoi/coachapp/
            CoachApplication.kt        # Hilt @HiltAndroidApp
            MainActivity.kt            # edge-to-edge, NavHost root
            core/
              network/
                ApiService.kt          # Retrofit @GET/@POST/@PATCH interfaces
                ApiClient.kt           # OkHttp + Retrofit builder
                ApiError.kt            # sealed class
                NetworkModule.kt       # @Module @InstallIn(SingletonComponent)
              auth/
                AuthStore.kt           # StateFlow<AuthState>, Hilt singleton
                SecureStorage.kt       # EncryptedSharedPreferences wrapper
                AuthModule.kt
              model/                   # @Serializable data classes
                User.kt
                Dashboard.kt
                Integration.kt
                Requests.kt
            features/
              onboarding/
                OnboardingScreen.kt
                OnboardingViewModel.kt
              home/
                HomeScreen.kt
                HomeViewModel.kt
              weekly/
                WeeklyScreen.kt
                WeeklyViewModel.kt
              trends/
                TrendsScreen.kt        # "준비 중" placeholder only
              goals/
                GoalsScreen.kt
                GoalsViewModel.kt
              settings/
                SettingsScreen.kt
                SettingsViewModel.kt
              feedback/
                FeedbackSheet.kt       # ModalBottomSheet
                FeedbackViewModel.kt
              workout/
                WorkoutSheet.kt        # ModalBottomSheet
            design/
              Color.kt                 # RcColors, ZoneColors, Zone enum
              Theme.kt                 # MaterialTheme wrapper, CompositionLocals
              Type.kt                  # RcTypography
              Spacing.kt               # Spacing, Radius objects
              components/
                RcTabBar.kt
                RcNavBar.kt
                RcCard.kt
                RcButton.kt
                RcZoneBadge.kt
                RcSectionHeader.kt
                ScoreSlider.kt
                AmbientBackground.kt
                RcScreen.kt
            navigation/
              AppNavigation.kt         # NavHost + routes
              Routes.kt                # @Serializable route objects
          res/
            font/
              pretendard_variable.ttf
              jetbrains_mono_variable.ttf
            values/
              strings.xml              # ko primary; en fallback in values-en/
      build.gradle.kts
    build.gradle.kts                   # version catalog, AGP
    gradle/libs.versions.toml
```

## Architecture

**MVVM with Jetpack ViewModel + StateFlow.**

Each screen owns one ViewModel. ViewModels are injected by Hilt via
`hiltViewModel()`. All screen state is expressed as a sealed class `UiState`:

```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val dashboard: DashboardResponse) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService,
    private val authStore: AuthStore,
) : ViewModel() {
    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        _state.value = HomeUiState.Loading
        _state.value = try {
            HomeUiState.Success(api.getDashboard())
        } catch (e: ApiError) {
            HomeUiState.Error(e.message ?: "알 수 없는 오류")
        }
    }
}
```

Screens collect state with `collectAsStateWithLifecycle()`.

**Auth** is a Hilt singleton `AuthStore`. It holds `StateFlow<AuthState>`
and writes to `SecureStorage` (EncryptedSharedPreferences). `ApiService`
calls read the key directly from `AuthStore` via an OkHttp interceptor.

**Navigation** lives in `AppNavigation.kt`. `MainActivity` sets up a single
`NavHost`. Route objects are `@Serializable` data objects/classes (type-safe
Navigation Compose 2.8+).

## Networking

```kotlin
// ApiService.kt
interface ApiService {
    @GET("v1/me")              suspend fun getMe(): User
    @GET("v1/me/dashboard")    suspend fun getDashboard(): DashboardResponse
    @GET("v1/me/integrations") suspend fun getIntegrations(): IntegrationsResponse
    @PATCH("v1/me/preferences") suspend fun updatePreferences(@Body req: UpdatePreferencesRequest): User
    @POST("v1/me/feedback")    suspend fun submitFeedback(@Body req: FeedbackRequest): Unit
    @POST("v1/me/goals")       suspend fun submitGoal(@Body req: GoalRequest): Unit
    @POST("v1/me/availability") suspend fun submitAvailability(@Body req: AvailabilityRequest): Unit
    @POST("v1/me/injuries")    suspend fun submitInjury(@Body req: InjuryRequest): Unit
    @POST("v1/runs/sync")      suspend fun triggerSync(@Body req: SyncRequest): Unit
    @POST("v1/users")          suspend fun createUser(@Body req: CreateUserRequest): CreateUserResponse
}
```

- Base URL: stored in `AuthStore`, default `http://10.0.2.2:8000` (Android
  emulator loopback for `localhost:8000`). Configurable via BuildConfig for
  release builds (`BuildConfig.API_BASE_URL`).
- Auth interceptor: reads `authStore.apiKey` synchronously via a blocking
  call from the interceptor; attaches `Authorization: Bearer <key>`.
- On HTTP 401: `authStore.signOut()`, re-emit `AuthState.SignedOut`. The
  root `NavHost` reacts by switching to the onboarding destination.
- JSON: `kotlinx.serialization`. Configure `JsonNamingStrategy.SnakeCase`
  or use explicit `@SerialName` annotations. Dates as `String`; parse
  ISO-8601 with `java.time.Instant`/`LocalDate`.

```kotlin
// NetworkModule.kt (Hilt)
@Provides @Singleton
fun provideRetrofit(authStore: AuthStore, @BaseUrl baseUrl: String): ApiService {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(authStore))
        .addInterceptor(HttpLoggingInterceptor().apply { level = Level.BODY })
        .build()
    val json = Json { ignoreUnknownKeys = true; namingStrategy = JsonNamingStrategy.SnakeCase }
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ApiService::class.java)
}
```

## Auth & Secure Storage

- `SecureStorage` wraps `EncryptedSharedPreferences` (Jetpack Security).
  Stores `apiKey` and `baseUrl` as strings. No unencrypted SharedPreferences.
- `AuthStore` is a Hilt singleton `@Singleton`. On creation it reads keychain
  synchronously (`runBlocking`) and emits the initial state.
- Keys: `"coach.apiKey"`, `"coach.baseUrl"`.
- `AuthState`: sealed class with `SignedOut` and `SignedIn(apiKey, baseUrl)`.

```kotlin
@Singleton
class AuthStore @Inject constructor(private val storage: SecureStorage) {
    private val _state = MutableStateFlow(storage.load())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val apiKey: String? get() = (_state.value as? AuthState.SignedIn)?.apiKey

    fun save(apiKey: String, baseUrl: String) {
        storage.save(apiKey, baseUrl)
        _state.value = AuthState.SignedIn(apiKey, baseUrl)
    }

    fun signOut() {
        storage.clear()
        _state.value = AuthState.SignedOut
    }
}
```

## Navigation

`AppNavigation.kt` holds a single `NavHost`. `MainActivity` collects
`authStore.state` and passes `startDestination` accordingly.

Destinations:

```kotlin
@Serializable object OnboardingRoute
@Serializable object MainRoute

// Within Main:
@Serializable object HomeRoute
@Serializable object WeeklyRoute
@Serializable object TrendsRoute
@Serializable object GoalsRoute
@Serializable object SettingsRoute

// Modals:
@Serializable data class WorkoutDetailRoute(val workoutId: String)
```

The main screen is a `Scaffold` with a custom `RcTabBar` in `bottomBar`.
Each tab content is a sub-`NavHost` or a single screen composable.

For the floating tab bar: render it as a `Box` overlaying the content with
`Alignment.BottomCenter`, placed inside the outer `Scaffold`'s `content`.
Avoid `bottomBar` if the Scaffold's implicit bottom padding fights the
floating appearance.

Feedback sheet: `ModalBottomSheet` triggered by FAB state on Home.
Workout detail: `ModalBottomSheet` (full-height).

## Screens

Every screen: skeleton loader during load, empty-state composable for empty
arrays / null data, error state with retry button. Match the visual layout of
the JSX prototype.

### 1. Onboarding

- Source: `onboarding.jsx`.
- `HorizontalPager` (Accompanist → Compose Foundation in 1.7+) for steps.
  5 steps: welcome → goal intro → baseline/paces → schedule/availability →
  API key entry.
- Final step: accepts API key and base URL. On submit calls
  `authStore.save(…)`. Root `NavHost` navigates to `MainRoute`.
- Progress indicator at top (linear or dot). No swipe back on step 1.

### 2. Home / 오늘

- Source: `home.jsx`.
- Endpoint: `GET /v1/me/dashboard`.
- Hero card: session name, zone badge, planned minutes, target pace.
  Zone accent drives card background tint and leading stripe.
- Recent activity card: top `recentActivities` entry if within ~24h,
  else "최근 활동 없음" empty state.
- Plan preview: next 2–3 `currentPlan` entries.
- FAB (52dp circle, bottom-right, above tab bar): opens `FeedbackSheet`.
- Pull-to-refresh: `PullToRefreshBox` (Material 3 1.3+) or
  `SwipeRefresh`-equivalent.

### 3. Weekly / 주간

- Source: `weekly.jsx`.
- Derived from `GET /v1/me/dashboard` → `currentPlan`. Group by `date`.
- 7-day strip: weekday label, date number, zone dot, planned minutes,
  rest icon if `isRest`. Horizontal scrollable row.
- Tap a day → `WorkoutDetailRoute(workoutId)`.
- Ship `strip` layout only in milestone 1.

### 4. Trends / 추이

- "준비 중" empty state only. Render the screen shell (nav bar, tab bar)
  with a centered card matching the design card style.

### 5. Goals / 목표

- Source: `goals.jsx`.
- Endpoints: `POST /v1/me/goals`, `POST /v1/me/blocks`.
- Goal card + block card visible if session-local cache has values.
  Tap → `ModalBottomSheet` edit form.
- No `GET` endpoint yet; cache last-submitted in ViewModel state
  (survives re-composition, lost on process death — acceptable for MVP).

### 6. Settings / 설정

- Source: `settings.jsx`.
- Endpoints: `GET /v1/me`, `PATCH /v1/me/preferences`,
  `GET /v1/me/integrations`, `POST /v1/runs/sync`,
  `POST /v1/me/availability`, `POST /v1/me/injuries`.
- Sections (use `LazyColumn` with `item` / `items`):
  1. **Profile**: `displayName`, `garminEmail`.
  2. **Preferences**: `timezone`, `locale`, `scheduleTimes`, `runMode`,
     `includeStrength`. Label `scheduleTimes` as "백엔드 코칭 체크인 시간".
  3. **Integrations**: `GET /v1/me/integrations`. Each row: provider name,
     status chip. Status → chip color (same logic as iOS doc above). No
     credential entry, no OAuth.
  4. **Inputs**: availability weekday editor and injury status editor.
  5. **Developer**: manual sync button, app version.
  6. **Sign out**: calls `authStore.signOut()`.
- Save is enabled only when form is dirty (track with `derivedStateOf`).
- **Do not** expose `plannerMode`, `llmProvider`, `llmModel`.

### 7. Workout Detail (ModalBottomSheet)

- Source: `workout.jsx`.
- Before state: name, zone badge, planned minutes, target pace, CTA
  "가민에서 실행" (closes sheet; no real timer).
- After state: planned vs. actual distance/pace/HR/duration,
  `executionQuality`, `targetMatchScore`. Data from `recentActivities`.
- Close button (glass pill style, top-trailing).

### 8. Daily Feedback (ModalBottomSheet)

- Source: `feedback.jsx`.
- `feedbackDate` defaults to today in user locale timezone.
- Five `ScoreSlider`s (fatigue, soreness, stress, motivation, sleep).
  Unset = omit from request payload.
- `painNotes`, `notes` multiline `OutlinedTextField`.
- Submit only non-null fields.

## Backend Scope Gap

Identical to iOS. See `IOS_APP_HANDOFF.md §Backend Scope Gap` for the full
table. Milestone-1 Android behavior matches iOS behavior exactly:
- Trends: "준비 중"
- Goals/Blocks: write-only + session cache
- Weekly: client-side date grouping of `currentPlan`

## Localization

- Strings: `res/values/strings.xml` (Korean, primary).
  `res/values-en/strings.xml` (English, fallback).
- Every user-visible string must use `stringResource(R.string.*)`. No
  hardcoded Korean or English literals in composables.
- Dates: `java.time` with `DateTimeFormatter` using `Locale.getDefault()`.
- Pace: `mm:ss/km`. Implement a `formatPace(seconds: Int): String` helper.

## Error Handling

```kotlin
sealed class ApiError : Exception() {
    data object Unauthorized : ApiError()
    data class Server(val status: Int, override val message: String?) : ApiError()
    data class Network(override val cause: Throwable?) : ApiError()
    data class Decoding(override val cause: Throwable?) : ApiError()
}
```

- On `Unauthorized`: call `authStore.signOut()`. Root NavHost reacts.
- Full-screen failures: show `EmptyState` composable with icon, message, retry.
- Inline submission failures: `Snackbar` or inline error text below the form.
- Never silently swallow exceptions. All `catch` blocks either update
  `UiState.Error` or log with `android.util.Log.e("CoachApp", …)`.

## Testing

- Unit tests: **JUnit 4** + **MockK** + `kotlinx-coroutines-test`.
- Minimum coverage for MVP:
  - `ApiService` request construction via OkHttp `MockWebServer`.
  - `Zone.toZone(string)` mapping, `formatPace`, score validation (1–10).
  - `AuthStore` state transitions (save, signOut, 401 flow).
  - ViewModel state transitions with a fake `ApiService`.
- Screenshot / UI tests: not required for MVP.

```kotlin
@Test fun `zone defaults to rest for unknown type`() {
    assertEquals(Zone.REST, "unknown".toZone())
}
```

## Build & Distribution

`android/gradle/libs.versions.toml` for centralized version management.

```toml
[versions]
kotlin = "2.1.0"
agp    = "8.7.0"
compose-bom = "2025.01.00"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
...
```

Two build variants:

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000\"")
    }
    release {
        buildConfigField("String", "API_BASE_URL", "\"${System.getenv("PROD_API_URL") ?: ""}\"")
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

Note: `10.0.2.2` is the Android emulator's alias for the host machine's
`localhost`. For a real device on the same network, use the machine's LAN IP.

Distribution: **Firebase App Distribution** or **Google Play Internal Testing**
for running-crew alpha testers. No public Play Store submission yet.

## Out of Scope (Do Not Build)

- Wear OS module of any kind.
- Google Sign-In, Firebase Auth, biometric unlock.
- Firebase Cloud Messaging / push notifications.
- In-app billing / Play Billing.
- Health Connect read or write (Garmin is the data source).
- Garmin or Google OAuth flows.
- LLM provider/model selector UI.
- Any screen that writes Garmin credentials.
- App Widgets, Tiles, complications.
- The `calendar` and `timeline` Weekly layout variants.
- Real workout execution / timer / GPS recording.

Dark mode is **in scope**. Both light and dark render correctly across all
screens.

## First Milestone — Done Criteria

1. App launches, reads `SecureStorage`, routes to onboarding or main tabs.
2. Design system in place: RcColors (light + dark), Pretendard + JetBrains
   Mono fonts bundled, zone colors, glass surface modifier, `RcScreen`
   scaffold, floating `RcTabBar`, scroll-driven `RcNavBar`, `RcCard`,
   `RcButton`, `RcZoneBadge`.
3. Both themes render correctly across all screens.
4. Onboarding flow completes; API key entry → transitions to main tabs.
5. Home dashboard: hero (correct zone accent), recent activity, plan preview,
   FAB → Feedback sheet.
6. Weekly tab: strip layout from `dashboard.currentPlan`.
7. Trends tab: "준비 중" empty state.
8. Goals tab: goal + block upsert forms; session-cached values display.
9. Settings tab: loads/saves preferences, integrations list with status chips,
   availability + injury inputs, manual sync, sign-out.
10. Workout detail sheet: before/after states from Home + Weekly.
11. Feedback sheet submits only fields the user set.
12. Korean and English strings in `strings.xml` for every visible label.
13. Unit tests for `toZone()`, `formatPace()`, score validation, auth
    state transitions, ViewModel loading states.
14. App installable on a physical Android device via App Distribution.

## Suggested Build Order

1. Project scaffold: Gradle BOM setup, Hilt modules, `SecureStorage`,
   `AuthStore`, `ApiService` interface + Retrofit builder, root `NavHost`.
2. Design system: `RcColors`, `RcTypography`, `Spacing`, `Radius`,
   `AmbientBackground`, `RcCard`, `RcButton`, `RcZoneBadge`, `RcTabBar`,
   `RcScreen` + `RcNavBar` scroll behaviour. Verify with `@Preview`.
3. Onboarding: pager + API key step → `authStore.save()` → `GET /v1/me`
   round-trip. Proves auth, networking, and design primitives together.
4. Home / 오늘: hero + activity card + plan preview + FAB.
5. Settings: preferences, integrations, availability, injury, sync, sign-out.
   Proves `PATCH`, dirty-state guard, and remaining `POST` endpoints.
6. Weekly: strip layout only.
7. Goals: race goal + block upsert forms.
8. Workout detail sheet (before/after).
9. Feedback sheet.
10. Trends empty state.
11. Localization pass, empty/error-state pass, App Distribution upload.
