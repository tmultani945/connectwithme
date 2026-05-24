# Sacred Flow

A belief-neutral Android MVP for personalized prayers, intentions, and spiritual reflections.

## Requirements

- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK 17** (Android Studio bundles this; if building from CLI, set `JAVA_HOME` to JDK 17)
- **Android SDK 35** (compileSdk) — install via Android Studio's SDK Manager
- **minSdk 24** (Android 7.0)

## First-time setup

1. Unzip the project and open the `sacred-flow` folder in Android Studio.
2. Android Studio will prompt you to install/sync missing SDK components — accept.
3. Wait for Gradle sync to complete. This downloads dependencies (~2–3 min on first run).
4. Run on an emulator (API 34+ recommended) or a physical Android device.

## What works out of the box

- The whole UI flow: onboarding, home, create, library, detail, reminders, settings, help, crisis resources, paywall.
- Local storage (Room), preferences (DataStore), and the daily reminder (WorkManager).
- The bundled offline fallback content shows whenever the network call fails.

## What requires extra setup to be fully functional

### 1. The LLM backend
The app posts to `https://api.sacredflow.app/v1/generate`. **This endpoint does not exist yet.**
You'll see fallback (offline) reflections every time you generate until you point the app at a real backend.

To stand up a minimal backend:
- Cloudflare Worker, Cloud Run, or Fly.io — about 150 lines of TypeScript/Python.
- It needs to accept the request shape in `data/remote/dto/ApiGenerateRequest.kt`, call an LLM (Claude, GPT, or similar) with the prompts from the spec, and return the response shape in `ApiGenerateResponse.kt`.
- For local testing, change `API_BASE_URL` in `app/build.gradle.kts` (debug build type) to point at your local dev server (use `http://10.0.2.2:PORT/` to reach localhost from an Android emulator).

### 2. Launcher icons
The project ships with placeholder XML icons. To customize:
- Right-click `app/src/main/res` in Android Studio → **New > Image Asset** → Launcher Icons (Adaptive and Legacy).

### 3. Google Play Billing
The app is wired to use Play Billing with product IDs `sacred_flow_plus_monthly` and `sacred_flow_plus_yearly`.
For purchases to work end-to-end, you need to:
- Publish at least an internal-testing build to Play Console.
- Register both products in Play Console under Subscriptions.
- Add yourself as a license tester to test purchases without being charged.
Until then, the billing flow will simply do nothing when you tap the Purchase button.

### 4. Network on debug
The debug build points at `https://staging-api.sacredflow.app/`. If you've stood up a local backend, edit the line in `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
```
…and add `android:usesCleartextTraffic="true"` to the `<application>` tag in `AndroidManifest.xml` for HTTP (not HTTPS) local dev.

## Architecture

See the full spec the project shipped with. In short:
- **Single module** Android app
- **Jetpack Compose** UI, Material 3
- **MVVM** with Hilt DI, sealed actions/events, StateFlow state
- **Room** for local storage, **DataStore** for cold-start preferences
- **Retrofit + OkHttp + kotlinx.serialization** for network
- **WorkManager** for daily reminders
- **Google Play Billing Library 7** for subscriptions

Key folders:
```
app/src/main/java/com/sacredflow/app/
├── core/          # dispatchers, clock, analytics, init, installid
├── data/          # local (Room), datastore, remote (Retrofit), repository, safety, fallback
├── domain/        # model + usecase
├── di/            # Hilt modules
├── reminder/      # WorkManager + notification
├── billing/       # Play Billing wrapper
└── ui/            # theme, navigation, components, screen/<feature>
```

## Building from CLI

```bash
./gradlew :app:assembleDebug          # debug APK at app/build/outputs/apk/debug/
./gradlew :app:installDebug           # install to connected device
./gradlew :app:test                   # run unit tests
```

If `./gradlew` doesn't exist (the wrapper jar isn't included for size reasons), run once from Android Studio first to generate it, or:
```bash
gradle wrapper --gradle-version 8.9
```

## License

Code is provided as a starter template. No license file is included; treat as proprietary until you decide.

## Known TODOs left in the codebase

- **Launcher icon:** placeholder. Replace before launch.
- **Privacy policy + Terms URLs:** Settings rows are stubbed.
- **Clear all data:** the action stub in `SettingsViewModel` shows a snackbar; full implementation deferred to v1.1.
- **"Use as template":** deferred from MVP — would pre-fill the Create form from a saved prayer.
- **Library export:** Plus feature stub.
- **Voice TTS:** Plus feature placeholder.

These are all called out in the spec as v1.1+ items.
