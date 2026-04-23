# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test

Gradle wrapper is committed; use it (not a system `gradle`):

- Build debug APK: `./gradlew assembleDebug`
- Install on connected device/emulator: `./gradlew installDebug`
- Full check (lint + unit tests): `./gradlew check`
- Unit tests (JVM): `./gradlew :app:testDebugUnitTest`
- Single unit test class: `./gradlew :app:testDebugUnitTest --tests "com.dragonest.artifacts.goo.ExampleUnitTest"`
- Instrumented tests (needs device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Android Lint: `./gradlew :app:lintDebug` (report in `app/build/reports/lint-results-debug.html`)
- Clean: `./gradlew clean`

Toolchain is pinned in `gradle/libs.versions.toml`: AGP 9.1.1, Kotlin 2.2.10, Compose BOM 2026.02.01, `compileSdk 36.1`, `minSdk 24`, Java 11 source/target. The Compose compiler is wired via the `org.jetbrains.kotlin.plugin.compose` plugin (not the legacy `composeOptions` block) — keep it that way when adding modules.

## Architecture

Single-module Android app (`:app`), Jetpack Compose UI, Kotlin only. Namespace and `applicationId`: `com.dragonest.artifacts.goo`.

Two activities declared in `AndroidManifest.xml`:

- `LoadingActivity` — the launcher activity (holds the `MAIN`/`LAUNCHER` intent filter). Entry point when the app starts.
- `MainActivity` — `exported="false"`, launched internally from `LoadingActivity`.

Both activities currently have empty `setContent { }` bodies — the UI has not been implemented yet. When adding screens, put composables under `com.dragonest.artifacts.goo` and theme code under `com.dragonest.artifacts.goo.ui.theme` (`PyramidRichesTheme` wrapper already exists in `ui/theme/Theme.kt`).

The `res/` directory contains the game's art and audio assets (Egyptian-themed backgrounds/elements, win/lose/music/slot sfx in `res/raw/`, custom `font/font.ttf`, button drawables for sound/music/pause/back/level toggles, a `score_bg`, and `popup_1`). The asset set implies a slot/pyramid-themed casual game with levels, scoring, pause, and sound toggles — useful context when wiring new screens, but none of this is coded yet.

Root Gradle project name in `settings.gradle.kts` is `"Pyramid(Riches"` (note the literal `(` character). Don't "fix" it unless intentional; changing it can invalidate IDE/Gradle caches.
