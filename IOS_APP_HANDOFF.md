# iOS App Handoff

This document is the iOS-specific implementation handoff. Product scope,
backend API contract, and cross-platform boundaries are defined in
`MOBILE_APP_HANDOFF.md` — read that first and treat it as the source of truth
for what the backend exposes. Visual direction comes from the design bundle
at `design/running-coach/` — read that next. This document defines *how* the
iOS app is built so the two align.

## Design Source & Fidelity

The design bundle at `design/running-coach/` was authored in Claude Design
(HTML/CSS/JS prototype). Primary files:

- `design/running-coach/README.md` — handoff instructions from the design
  tool. Says: read the chat transcripts, read the primary HTML, follow its
  imports, recreate visually pixel-perfect in native code. Do not copy the
  prototype's internal React structure verbatim.
- `design/running-coach/chats/chat1.md` — the full design conversation. The
  user's stated direction lives here (iOS 26 Liquid Glass, light default,
  Pretendard + JetBrains Mono, zone-coded accents, floating tab bar, large
  title → floating nav bar).
- `design/running-coach/project/Running Coach.html` — primary prototype.
- `design/running-coach/project/src/tokens.jsx` — design tokens (colors,
  typography, zones, materials). Treat this as authoritative for values.
- `design/running-coach/project/src/primitives.jsx` — shared components
  (status bar, phone frame, tab bar, cards, buttons, nav bar, screen
  scaffold). Treat as authoritative for component geometry.
- `design/running-coach/project/src/{home,weekly,trends,goals,settings,
  workout,feedback,onboarding}.jsx` — per-screen layouts.

Implementation rule: match the **visual output** (colors, spacing, type,
geometry, interaction). Don't port the JSX structure; translate it to idiomatic
SwiftUI.

## Target & Audience

- Primary target users: Korean 20–30s running crews. iPhone share in this
  demographic is materially higher than the national average.
- Most target users already own a Garmin watch. Garmin handles workout
  execution (HR, GPS, intervals). The iOS app is a *coaching companion*, not a
  workout executor.
- Because of that, **Apple Watch support is out of scope for the first
  milestone**. Do not build a watchOS target yet.

## Stack & Versions

- Language: **Swift 6**. Enable strict concurrency checking.
- UI: **SwiftUI**. Do not use UIKit unless a specific control is unavailable
  in SwiftUI; in that case wrap via `UIViewRepresentable` locally.
- State: **`@Observable` macro** (Observation framework, iOS 17+). Do not use
  `ObservableObject` / `@Published` / `Combine`.
- Concurrency: **`async` / `await`**. Do not use Combine publishers for
  networking or view state.
- Minimum iOS version: **iOS 17.0**. This unlocks `@Observable`, `#Preview`,
  `SwiftData` (if needed later), and `NavigationStack` type-safe routing.
- Xcode: latest stable (16.x or newer).
- Dependencies: prefer Apple-native frameworks. URLSession (networking),
  SwiftUI, Observation, Foundation, Security (Keychain), and `os.log` cover
  the first milestone without any third-party code. Add an external
  dependency only when a problem cannot be reasonably solved with the
  standard library.

## Design System

Values here come from `design/running-coach/project/src/tokens.jsx` and
`primitives.jsx`. Centralize them in `Theme.swift` and reference via
`@Environment(\.theme)` or equivalent — do not scatter hex literals through
views.

### Themes

Both **light** (default) and **dark** must be supported. The user's system
setting drives the choice; a manual toggle lives in Settings. Do not treat
dark mode as optional — it's part of the design direction.

### Colors

```
LIGHT THEME
  bg              #F2EFE8  (warm off-white base)
  bgGrad          radial-gradient(120% 80% at 20% 0%, #FFFFFF 0%, #F2EFE8 55%, #E6E2D8 100%)
  bgElev          #FFFFFF
  bgElev2         #FAF8F3
  text            #0B1220
  textDim         rgba(11,18,32,0.68)
  textFaint       rgba(11,18,32,0.46)
  textMuted       rgba(11,18,32,0.30)
  border          rgba(11,18,32,0.08)
  borderStrong    rgba(11,18,32,0.16)
  divider         rgba(11,18,32,0.08)

DARK THEME
  bg              #0B1220  (deep navy)
  bgGrad          radial-gradient(120% 80% at 20% 0%, #1A2548 0%, #0B1220 55%, #050811 100%)
  bgElev          #121A2B
  bgElev2         #1A2236
  text            #F5F3EE  (warm off-white)
  textDim         rgba(245,243,238,0.72)
  textFaint       rgba(245,243,238,0.50)
  textMuted       rgba(245,243,238,0.32)
  border          rgba(255,255,255,0.10)
  borderStrong    rgba(255,255,255,0.18)
  divider         rgba(255,255,255,0.08)
```

### Zone Accents (same values, both themes)

Every Today/workout screen picks a single accent tied to the session type.

```
recovery    #4A9EDB  회복   (soft  rgba(74,158,219,0.14),  softStrong 0.24)
base        #3FB87F  베이스 (soft  rgba(63,184,127,0.14),  softStrong 0.24)
threshold   #E8A94C  역치   (soft  rgba(232,169,76,0.16),  softStrong 0.26)
interval    #E35D5D  인터벌 (soft  rgba(227,93,93,0.14),   softStrong 0.24)
rest        #8A8F99  휴식   (soft  rgba(138,143,153,0.14), softStrong 0.24)
long        #8F7BD4  장거리 (soft  rgba(143,123,212,0.14), softStrong 0.24)
```

Backend `sessionType` values map to these. If the backend emits an
unrecognized type, fall back to `rest`.

### Typography

- UI font: **Pretendard Variable**. Fallback chain: `-apple-system`, `system`.
  Ship the font as a bundled resource (`Pretendard-Variable.ttf`) and
  register via `Info.plist` `UIAppFonts`. Do not rely on network fonts.
- Data font: **JetBrains Mono** (400/500/600). Used for paces, durations,
  distances, HR — anything numeric that needs alignment.
- Enable tabular numerals for all numeric UI with
  `.monospacedDigit()` or the custom `rc-tnum` style. JetBrains Mono
  already has tabular figures; Pretendard numerics should still be rendered
  tabular via `.monospacedDigit()`.
- Large title: 32pt / weight 700 / letter-spacing -0.8.
- Nav title (collapsed): 17pt / weight 600 / -0.3.
- Section eyebrow: 11pt / weight 600 / +1.2 / uppercase.
- Body: 15pt regular. Dim copy uses `textDim`.
- Metric headline: 40–56pt / JetBrains Mono / 500–600 weight.

### Spacing & Radii

- Screen horizontal padding: **20pt**.
- Card radius: **22pt** standard, **28–34pt** for hero surfaces.
- Button radius: sm 18 / md 25 / lg 28.
- Tab bar pill radius: **30pt**, inset 12pt from side edges, 18pt from bottom.
- FAB: 52pt circle, bottom 100pt, right 20pt (above floating tab bar).

### Liquid Glass Materials (iOS 26 style)

This is the central visual motif. Apply consistently to tab bar, nav bar on
scroll, cards, and secondary buttons.

Light:
```
bg:      rgba(255,255,255,0.55)   (rgba(255,255,255,0.62) for cards)
filter:  blur(24–30px) saturate(170–180%)
border:  0.5px rgba(11,18,32,0.08)
shadow:  0 16px 48px rgba(11,18,32,0.12),
         0 1px 0 rgba(255,255,255,0.9) inset,
         0 0 0 0.5px rgba(255,255,255,0.7) inset
```

Dark:
```
bg:      rgba(18,26,44,0.55)       (rgba(28,38,60,0.55) for cards)
filter:  blur(24–30px) saturate(170–180%)
border:  0.5px rgba(255,255,255,0.10)
shadow:  0 20px 60px rgba(0,0,0,0.45),
         0 1px 0 rgba(255,255,255,0.08) inset,
         0 0 0 0.5px rgba(255,255,255,0.12) inset
```

Card specular highlight (both themes): a 1pt hairline line inset 14pt from
each side at the top edge, horizontal gradient
`transparent → rgba(255,255,255,0.9 light / 0.18 dark) → transparent`.

SwiftUI implementation notes:
- Use `.background(.ultraThinMaterial)` or `.regularMaterial` only if it
  matches visually; otherwise render a translucent `Color` + blur via
  `.background(Rectangle().fill(…).blur(…))` or a `UIVisualEffectView`
  wrapper. Test both — iOS 17+ Materials are often sufficient.
- The specular highlight is a tiny Rectangle at the top of the card, not a
  `Material` feature.
- Behind glass surfaces, always render the ambient zone-tinted wash (see
  Phone frame in primitives.jsx) so blur has something to refract.

### Ambient Background Wash

The root scene has three soft radial tints layered behind content so Liquid
Glass has texture to refract:

```
radial-gradient(120% 60% at 0% 0%,   rgba(74,158,219,0.12–0.14), transparent 55%)
radial-gradient(100% 50% at 100% 15%, rgba(232,169,76,0.09–0.12), transparent 60%)
radial-gradient(140% 70% at 50% 110%, rgba(143,123,212,0.10–0.12), transparent 60%)
```

Implement as three soft, blurred ellipses placed in a `ZStack` below main
content, or as a single `CAGradientLayer`-equivalent composite. Animate
subtly or leave static.

### Screen Scaffold (RCScreen equivalent)

Every primary tab screen follows this structure — match
`primitives.jsx RCScreen`:

- Background: ambient wash + theme bg.
- Status bar is an overlay; content scrolls *underneath* edge-to-edge.
- When `scrollOffset > 28`, a floating glass nav bar fades in at the top
  with blur + specular + hairline border. Nav title becomes visible at the
  same time.
- Initial view shows an **eyebrow** line (section context, 11pt uppercase)
  followed by a **large title** (32pt, 700). Both live inside the scrollable
  content, not pinned.
- Floating tab bar overlays at the bottom. Screens must add
  `bottom content inset = 120pt` so content doesn't hide behind it.

Build this as a reusable SwiftUI view, e.g. `RCScreen { … }` with
`title: String`, `eyebrow: String?`, `leading:`, `trailing:`.

### Components to Build (match primitives.jsx)

- `StatusBar` overlay (dynamic island respected; content passes through).
- `HomeIndicator` bar at bottom-center.
- `RCTabBar` — floating glass pill, 5 tabs, active pill tint.
- `RCNavBar` — appears on scroll, glass background.
- `RCScreen` — large title + scroll-driven nav bar.
- `RCCard` — translucent material card with specular hairline, optional
  3pt accent strip on the leading edge.
- `RCButton` — primary (solid, theme-inverted), secondary (glass pill),
  ghost.
- `RCZoneBadge` — session-type chip, filled or soft.
- `RCSectionHeader` — 11pt uppercase eyebrow + optional subtitle + right
  action.
- `RCDivider`, `RCIcon` (SF Symbols are fine for iOS; the prototype inlines
  SVGs, translate to SF Symbols equivalents where possible).

## Project Layout

Single Xcode project at `ios/`, single app target.

```
ios/
├── CoachApp.xcodeproj
├── CoachApp/
│   ├── App/
│   │   ├── CoachAppApp.swift          # @main entry
│   │   └── RootView.swift             # auth gate → onboarding or main
│   ├── Core/
│   │   ├── Networking/
│   │   │   ├── APIClient.swift
│   │   │   ├── APIError.swift
│   │   │   └── Endpoints.swift
│   │   ├── Auth/
│   │   │   ├── AuthStore.swift        # @Observable, owns apiKey state
│   │   │   └── Keychain.swift         # thin wrapper over Security framework
│   │   └── Models/                    # Codable DTOs matching backend
│   │       ├── User.swift
│   │       ├── Dashboard.swift
│   │       ├── Integration.swift
│   │       ├── Activity.swift
│   │       ├── PlannedWorkout.swift
│   │       ├── Feedback.swift
│   │       ├── Availability.swift
│   │       ├── Goal.swift
│   │       ├── Injury.swift
│   │       └── Block.swift
│   ├── Features/
│   │   ├── Onboarding/                # 5-step onboarding + API key flow
│   │   ├── Home/                      # 오늘 (Today) tab
│   │   ├── Weekly/                    # 주간 tab
│   │   ├── Trends/                    # 추이 tab (ACWR etc.)
│   │   ├── Goals/                     # 목표 tab
│   │   ├── Settings/                  # 설정 tab (houses Integrations, prefs, inputs)
│   │   ├── Workout/                   # modal: workout detail (before/after)
│   │   └── Feedback/                  # modal: daily feedback
│   ├── Design/
│   │   ├── Theme.swift                # tokens: colors, typography, zones
│   │   ├── Materials.swift            # liquid-glass modifiers
│   │   └── Components/                # RCScreen, RCTabBar, RCCard, RCButton, RCZoneBadge, …
│   └── Resources/
│       ├── Fonts/
│       │   ├── PretendardVariable.ttf
│       │   └── JetBrainsMono-VariableFont_wght.ttf
│       ├── Localizable.xcstrings      # Korean primary, English secondary
│       └── Assets.xcassets
└── CoachAppTests/
    └── APIClientTests.swift
```

Each feature folder contains one SwiftUI `View` and (if needed) one
`@Observable` store. Co-locate; no shared `ViewModels/` folder.

## Architecture

Follow Apple's SwiftUI + Observation patterns as shown in WWDC sessions and
sample code (Food Truck, Backyard Birds, Destination Video).

- Views are `struct … : View` and own local state with `@State`, `@Bindable`,
  and `@Environment`.
- Non-trivial screen state lives in an `@Observable` class co-located with
  the screen (e.g. `HomeStore`). Simple screens can keep state entirely in
  `@State`.
- Shared app-wide state (`AuthStore`, `APIClient`, `Theme`) is created at
  the root and injected via `@Environment`.
- Navigation uses `NavigationStack` with typed `navigationDestination(for:)`
  paths. One stack per tab.
- Modals (workout detail, feedback, onboarding) are presented via
  `.sheet(item:)` or `.fullScreenCover(item:)`.

Example root wiring:

```swift
@main
struct CoachAppApp: App {
    @State private var auth = AuthStore()
    @State private var api = APIClient()
    @State private var theme = Theme.system

    var body: some Scene {
        WindowGroup {
            RootView()
                .environment(auth)
                .environment(api)
                .environment(theme)
        }
    }
}
```

## Networking

One `APIClient` class. Each store tracks its own loading/error state.

- Base URL read from `Info.plist` key `API_BASE_URL`, overridable per build
  config (Debug → local backend, Release → prod).
- Every request attaches `Authorization: Bearer <apiKey>` from `AuthStore`.
  If `apiKey` is missing, `APIClient` throws `APIError.unauthenticated`
  without hitting the network.
- Use `URLSession.shared.data(for:)` with `async throws`.
- Backend returns camelCase (`userId`, `nextRunAt`) — use the default
  `JSONDecoder`. Use `.iso8601` strategy for ISO8601 timestamp fields;
  decode date-only fields (`"2026-04-25"`) as `String` and parse explicitly
  with a `DateFormatter` configured to `yyyy-MM-dd` / UTC.
- On HTTP 401 → clear keychain, flip `AuthStore` to signed-out, propagate
  `APIError.unauthenticated`. The root view reacts by showing onboarding.
- On HTTP 4xx/5xx → throw `APIError.server(status: Int, message: String?)`.
  Try to decode a `{"error": "..."}` body; fall back to status text.

Endpoint surface (see `MOBILE_APP_HANDOFF.md` for payloads):

| Method | Path                     | Purpose                        |
|--------|--------------------------|--------------------------------|
| POST   | `/v1/users`              | Create dev user (onboarding)   |
| GET    | `/v1/me`                 | Profile                        |
| GET    | `/v1/me/dashboard`       | Home screen data               |
| GET    | `/v1/me/integrations`    | Integrations list              |
| PATCH  | `/v1/me/preferences`     | Settings update                |
| POST   | `/v1/runs/sync`          | Manual sync (debug)            |
| POST   | `/v1/me/feedback`        | Daily feedback                 |
| POST   | `/v1/me/availability`    | Weekday availability           |
| POST   | `/v1/me/goals`           | Race goal upsert               |
| POST   | `/v1/me/blocks`          | Training block upsert          |
| POST   | `/v1/me/injuries`        | Injury status upsert           |

## Auth & Keychain

- `AuthStore` is `@Observable`. Holds `apiKey: String?`, `user: User?`,
  `isSignedIn: Bool { apiKey != nil }`.
- `Keychain.swift` wraps `kSecClassGenericPassword` reads/writes for a single
  service key (`"app.coach.apiKey"`). No third-party wrapper.
- On app launch, `AuthStore.init` reads the keychain synchronously (fast, KB
  of data) and hydrates `apiKey`.
- On sign-out or 401: delete keychain entry, set `apiKey = nil`, set
  `user = nil`.
- **Do not** store the API key in `UserDefaults`. Keychain only.

## Navigation

The design uses 5 bottom tabs (see `primitives.jsx RC_TABS` and `app.jsx`):

1. **오늘 / Home** — today's session, recent activity summary.
2. **주간 / Weekly** — 7-day plan view (strip / calendar / timeline layouts
   exist in the prototype; ship `strip` first).
3. **추이 / Trends** — charts: ACWR, volume, pace trends.
4. **목표 / Goals** — race goals & training blocks.
5. **설정 / Settings** — preferences, integrations, inputs (availability /
   injuries), sign-out.

Modal/overlay surfaces:

- **Workout detail** — opened from Home hero tap and Weekly day tap.
  Full-screen sheet. Has before/after states (`completed: Bool`).
- **Daily feedback** — opened by tapping the FAB on Home. Full-screen sheet.
- **Onboarding** — first-run-only 5-step flow. Takes over the entire window
  until complete.

Top-level routing:

- `RootView` switches on `auth.isSignedIn`.
  - Not signed in → `OnboardingFlow` (which ends with an API key entry step).
  - Signed in → `MainTabView`.
- Each tab owns its own `NavigationStack` with a typed path enum.

## Screens

Every screen must render a `redacted` skeleton during load, show empty-state
copy when the backend returns empty arrays / null timestamps, and surface
errors with a `ContentUnavailableView` + retry button.

Screen layouts mirror the design prototype; translate visually, not
structurally.

### 1. Onboarding (5 steps)

- Source: `design/running-coach/project/src/onboarding.jsx`.
- Steps (match design order): welcome → goal intro → baseline/paces →
  schedule/availability → permissions & API key.
- The final step collects the API key (or creates a dev user via
  `POST /v1/users`) and on success hands off to the main tabs.
- Steps are swipeable / button-progressed. Progress indicator at top.
- **No OAuth**, no HealthKit prompts, no push permission requests in this
  milestone — follow the "Out of Scope" list.

### 2. Home / 오늘

- Source: `design/running-coach/project/src/home.jsx`.
- Endpoint: `GET /v1/me/dashboard`.
- Hero section (accent = today's `sessionType`, fallback `rest`):
  - Date eyebrow, large title "오늘의 운동" / localized equivalent.
  - Session name, planned minutes, target pace, expected distance if known.
  - Zone badge.
  - "시작하기" button (for MVP, just opens Workout detail modal; no real
    workout execution — Garmin does that).
- Yesterday / recent activity card:
  - If `recentActivities` has an item within ~24h: show completion summary
    (distance, avg pace, HR, `executionStatus`, `targetMatchScore`).
  - Else: "최근 활동 없음" empty state.
- Plan preview: next 2–3 `currentPlan` entries collapsed under the hero.
- FAB (52pt circle, bottom-right, above the tab bar): opens the Feedback
  modal.
- Pull-to-refresh reloads the dashboard.
- Manual sync lives in Settings, not here (contradicts earlier draft).

### 3. Weekly / 주간

- Source: `design/running-coach/project/src/weekly.jsx`.
- For MVP, derive from `GET /v1/me/dashboard` → `currentPlan` (array).
  Group entries by date and render a 7-day strip.
- Each day cell: weekday label, date, zone dot, planned minutes, rest icon
  if `isRest`.
- Tapping a day opens the Workout detail modal populated from that entry.
- Three layout variants exist in the prototype (`strip`, `calendar`,
  `timeline`). **Ship `strip` only** in milestone 1; the other two are
  deferred.

### 4. Trends / 추이

- Source: `design/running-coach/project/src/trends.jsx`.
- **Requires backend additions** — see "Backend Scope Gap" below. The
  backend does not currently expose ACWR, weekly volume history, or pace
  trends.
- For milestone 1: ship the screen with a clear "곧 제공됩니다 / Coming
  soon" empty state that matches the design card style. Do not fake data.
  Do not wire to a placeholder endpoint.

### 5. Goals / 목표

- Source: `design/running-coach/project/src/goals.jsx`.
- Endpoints: `POST /v1/me/goals` (upsert), `POST /v1/me/blocks` (upsert).
- Current race goal card: `goalName`, `raceDate`, `distance`, `goalTime`,
  `targetPace`, days-to-race countdown. Tap → edit sheet.
- Active training block card: `phase`, `startsOn`–`endsOn`, `focus`,
  `weeklyVolumeTargetKm`. Tap → edit sheet.
- Edit sheets are standard forms with save/cancel.
- Backend currently has no `GET` for goals/blocks; treat this screen as
  write-only until backend catches up. Cache the last submitted values
  locally for the session so the UI can show what was saved.

### 6. Settings / 설정

- Source: `design/running-coach/project/src/settings.jsx`.
- Endpoints: `GET /v1/me`, `PATCH /v1/me/preferences`,
  `GET /v1/me/integrations`, `POST /v1/runs/sync`,
  `POST /v1/me/availability`, `POST /v1/me/injuries`.
- Sections:
  1. **Profile**: `displayName`, `garminEmail`.
  2. **Preferences**: `timezone`, `locale`, `scheduleTimes`, `runMode`,
     `includeStrength`. Label `scheduleTimes` explicitly as
     **"백엔드 코칭 체크인 시간 (푸시 알림 아님)"**.
  3. **Integrations**: row list from `GET /v1/me/integrations`. Each row:
     provider icon, `displayName`, status chip. Status → chip color:
     - `active` / `configured` / `env_compat` → connected (green)
     - `reauth_required` → amber "재연결 필요"
     - `error` → red, show `lastError` beneath
     - `disabled` / `not_configured` → gray "미설정"
     - `coming_soon` → gray "준비 중", row visually disabled
     Do not render secrets. Do not expose credential entry.
  4. **Inputs**: availability editor (weekday picker + fields) and injury
     status editor (both call their respective endpoints).
  5. **Developer**: manual sync button → `POST /v1/runs/sync`, mode `auto`.
     App version, debug menu. Hide behind a long-press or build-flag gate
     if it shouldn't ship to alpha testers.
  6. **Sign out**: clears keychain, returns to onboarding.
- **Do not** expose `plannerMode`, `llmProvider`, or `llmModel`.
- Save button is enabled only when the form is dirty.

### 7. Workout Detail (modal)

- Source: `design/running-coach/project/src/workout.jsx`.
- Opened from Home hero or a Weekly day cell.
- Has **before** and **after** states:
  - Before: session name, zone badge, planned minutes, target pace, coach
    notes if present, "시작하기 (가민에서 실행)" CTA that just closes the
    sheet. No real timer.
  - After: comparison view — planned vs. actual distance / pace / HR /
    duration / `executionQuality` / `targetMatchScore`. Pulls from the
    matching `recentActivities` entry.
- Back button (glass pill, top-left) closes the sheet.

### 8. Daily Feedback (modal)

- Source: `design/running-coach/project/src/feedback.jsx`.
- Endpoint: `POST /v1/me/feedback`.
- Opened from the Home FAB.
- Fields:
  - `feedbackDate` — default today in user timezone.
  - Five score fields: `fatigueScore`, `sorenessScore`, `stressScore`,
    `motivationScore`, `sleepQualityScore`. Optional integer 1–10. Use a
    `ScoreSlider` component (unset / 1–10). Client-side validation.
  - `painNotes`, `notes` — multiline text.
- Submit sends only fields the user actually set (omit nils).

## Backend Scope Gap

The design expects data the backend doesn't currently expose. Flag these to
the product owner and treat as milestone-2+ until endpoints land:

| Design need                          | Backend state                                                       | Milestone-1 behavior                |
|--------------------------------------|---------------------------------------------------------------------|-------------------------------------|
| ACWR value + sparkline               | Not exposed                                                         | Trends screen shows "준비 중"       |
| Weekly volume history chart          | Not exposed                                                         | Trends screen shows "준비 중"       |
| Pace trend over time                 | Not exposed                                                         | Trends screen shows "준비 중"       |
| Structured 7-day plan with zones     | `dashboard.currentPlan` is flat; `sessionType` is present           | Group client-side by date           |
| Workout detail by id                 | Not exposed; `recentActivities` is the only source                  | Look up by `providerActivityId`     |
| Score chips (회복 / 피로 / 부상 risk) | Not exposed as computed values                                     | Show only raw feedback scores       |
| Read of active goal / block          | Only POST upsert exists                                             | Cache last-submitted values         |
| Pro workout plan per day (intervals) | Not exposed                                                         | Show planned total minutes only     |

Milestone 2 backend asks (in priority order):
1. `GET /v1/me/trends` → { acwr, weeklyVolume[], paceTrend[] }.
2. `GET /v1/me/goals` and `GET /v1/me/blocks` (reads for upsert endpoints).
3. `GET /v1/me/plan?from=&to=` → structured 7-day plan with per-day intervals.
4. Score-chip computed values on `GET /v1/me/dashboard`.

## Localization

- Primary language: **Korean (`ko`)**. English (`en`) is the fallback.
- Use **String Catalogs** (`Localizable.xcstrings`, Xcode 15+). Do not use
  legacy `.strings` files.
- Every user-visible string must come from the catalog — no hardcoded Korean
  or English literals in views.
- Date / time / number formatting uses `Date.FormatStyle` and
  `Measurement.FormatStyle` with the user's current locale.
- Pace formatting (`mm:ss/km` or `mm:ss/mi`) is app-specific; implement a
  small helper and unit test it. Default unit is km for Korean locale.

## Error Handling

- One top-level error type: `APIError` (`unauthenticated`, `network`,
  `decoding`, `server(status, message)`).
- Views display errors via `ContentUnavailableView` for full-screen failures,
  inline alerts for submission failures.
- Never silently swallow errors. Every `catch` either shows UI or logs to
  `os.Logger` with a subsystem of `"app.coach"`.
- On 401 from anywhere, `AuthStore.signOut()` is called and the user is
  bounced to onboarding. Do not show a toast; the screen change is the signal.

## Testing

- Unit tests with **Swift Testing** (`import Testing`), not XCTest.
- Minimum coverage for MVP:
  - `APIClient` request construction (URL, headers, body encoding) via a
    stubbed `URLProtocol`.
  - Score validation (1–10), pace formatting, zone mapping from
    `sessionType`, and date-only decoding helpers.
- UI snapshot tests are **not required** for MVP.
- Do not add integration tests that hit the live backend.

## Build & Distribution

- Two build configurations: `Debug` (points at local backend,
  `API_BASE_URL=http://localhost:8000`) and `Release` (points at prod URL,
  via `.xcconfig` or CI env var — do not commit prod URLs publicly).
- Code signing: Apple Developer Program team set in project settings.
  Leave signing as "Automatic"; do not commit team-specific identifiers.
- Distribution: **TestFlight** for running-crew alpha testers. No App
  Store submission yet.
- CI is out of scope for MVP. Local archive + Xcode Organizer upload is fine.

## Out of Scope (Do Not Build)

- watchOS target of any kind.
- Apple Sign In, social login, password flows, biometric unlock.
- Push notifications / `UNUserNotificationCenter` registration.
- In-app purchases / StoreKit.
- HealthKit read or write (explicit product decision: Garmin is the data
  source for target users).
- Garmin or Google OAuth flows.
- LLM provider/model selector UI.
- Any screen that writes Garmin credentials.
- Widgets, App Clips, Live Activities, Dynamic Island content.
- The `calendar` and `timeline` Weekly layout variants (ship `strip` only).
- Real workout execution / timer / GPS recording.

Dark mode is **in** scope (part of the design). Build both themes.

## First Milestone — Done Criteria

Milestone 1 is shippable to TestFlight when all of the following are true:

1. App launches, reads keychain, routes to onboarding or main tabs.
2. Design system is in place: colors, typography (Pretendard + JetBrains
   Mono bundled), zones, liquid-glass materials, `RCScreen` scaffold,
   floating tab bar, floating nav bar, cards, buttons, zone badges.
3. Both light and dark themes render correctly across every screen.
4. Onboarding flow completes; final step accepts an API key (or creates a
   dev user) and transitions to main tabs.
5. Home dashboard renders hero (with correct zone accent), recent activity
   card, and plan preview. FAB opens Feedback modal.
6. Weekly tab renders the `strip` layout from `dashboard.currentPlan`.
7. Trends tab shows the "준비 중" empty state in the design card style.
8. Goals tab submits goal and block upserts; cached values display locally.
9. Settings tab loads/saves preferences, renders integrations list with
   correct status chips, submits availability and injury data, has manual
   sync and sign-out.
10. Workout detail modal opens from Home + Weekly with before/after states.
11. Feedback modal submits with only the fields the user set.
12. Korean and English present in the String Catalog for every user-visible
    string.
13. `APIClient` unit tests pass. Pace / score / zone / date helpers
    unit-tested.
14. App archivable and installable via TestFlight on the developer's device.

## Suggested Build Order

1. Project scaffold, `APIClient`, `AuthStore`, `Keychain`, root routing.
2. `Theme.swift` + core design primitives (`RCScreen`, `RCTabBar`,
   `RCNavBar`, `RCCard`, `RCButton`, `RCZoneBadge`, ambient wash). Verify
   both light and dark with `#Preview`.
3. Onboarding flow ending in API key entry (or dev-user creation) →
   `GET /v1/me` round-trip. Proves auth, networking, and design primitives
   together.
4. Home / 오늘 — hero + recent activity card + plan preview + FAB.
5. Settings — preferences, integrations list, availability, injury,
   manual sync, sign-out. Proves `PATCH`, dirty-state UX, and all remaining
   `POST` endpoints via Settings inputs.
6. Weekly — strip layout only.
7. Goals — race goal + training block upsert forms.
8. Workout detail modal (before/after).
9. Feedback modal.
10. Trends empty state.
11. Localization pass, empty/error-state pass, TestFlight upload.
