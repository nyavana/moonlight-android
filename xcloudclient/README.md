# Better XCloud Android Client

This module provides a dedicated Android client for running the community **Better XCloud** browser script inside a Chromium-backed WebView. The app is designed for living-room setups where a single controller-focused tab is all that is required.

## Feature highlights

- **Launch hub** – When the application starts you are greeted with a streamlined screen that exposes the two primary flows: _Start XCloud_ and _Options_.
- **Single-tab Chromium experience** – Once streaming begins the app locks the embedded Chromium instance to `https://www.xbox.com/en-US/play`, injects the Better XCloud user script, and hides every system bar for an immersive experience.
- **Controller kernel switching** – Choose between the native Android controller pipeline and the Better XCloud web kernel. The selection is exposed to the user script through `window.__betterXcloudConfig` so that both strategies can coexist.
- **Configurable key mapping** – Remap every standard Xbox button to a JavaScript `KeyboardEvent.code` value. The mapping is persisted via DataStore and forwarded to the injected script.
- **Quality-of-life extras** – Keep the screen awake while streaming, enforce landscape orientation, and automatically enable WebView debugging in debug builds. These defaults can be tuned inside the Options screen.

Additional enhancements such as theming, controller status summaries, and blocking navigation away from Xbox domains are implemented to make the experience more console-like.

## Project layout

```
xcloudclient/
├── build.gradle                # Standalone Gradle module using Jetpack Compose
├── README.md                   # This document
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    ├── assets/
    │   └── better_xcloud.user.js
    ├── java/com/betterxcloud/client/
    │   ├── MainActivity.kt            # Activity + Compose theme
    │   ├── XCloudApplication.kt       # Enables WebView debugging in debug builds
    │   ├── browser/XCloudBrowser.kt   # Chromium wrapper and script injector
    │   ├── data/                      # Preferences and enums
    │   └── ui/                        # Composable screens and state container
    └── res/values/
        ├── strings.xml
        └── themes.xml
```

## Building & running

1. Ensure the top-level project is synced (`./gradlew tasks`).
2. Select the `xcloudclient` module in Android Studio (or run `./gradlew :xcloudclient:assembleDebug`).
3. Install the generated APK on your device and grant network permissions when prompted.

The embedded Chromium instance automatically launches full screen and navigates to the XCloud portal with the Better XCloud script injected. Replace `src/main/assets/better_xcloud.user.js` with the latest official script to get the full experience.

## Extending the client

- **Custom kernels** – Implement alternate controller strategies by parsing `window.__betterXcloudConfig.kernel` inside the userscript.
- **Additional bindings** – `XCloudPreferencesRepository` is the single source of truth. Persist extra toggles there and surface them through Compose in `OptionsScreen`.
- **Diagnostics overlay** – Compose makes it straightforward to add overlays to `BrowserScreen` (e.g. latency stats, connection indicators).

Every Kotlin file is heavily commented to explain the rationale behind architectural decisions and to make future contributions approachable.
