# Spoty Rebrand — What Was Actually Done

This replaces the old "6 files only" claim from CHANGES_NEEDED.md — that number was wrong.
421 Kotlin files + manifest referenced `com.music.vivi`. All of them were touched.

## Automated (done, in this zip)

1. Package renamed: `com.music.vivi` → `com.infinity.spoty` (directory moved + every
   package/import statement + AndroidManifest.xml action strings + build.gradle.kts namespace)
2. `applicationId`: `com.vivi.vivimusic` → `com.infinity.spoty`
3. `app_name.xml` (main + debug): `VIVI` / `VIVI Debug` → `Spoty` / `Spoty Debug`
4. Links in `AboutScreen.kt`:
   - GitHub repo: `vivizzz007/vivi-music` → `i7rvn/Spoty`
   - GitHub profile: `github.com/vivizzz007` → `github.com/i7rvn`
   - Telegram: `t.me/vivimusicapp` → `t.me/vrni2`
   - ko-fi link **left untouched** (`ko-fi.com/vividhpashokan`) — no replacement was ever given to me,
     decide if you're keeping it or need your own ko-fi handle
5. Updater (`vivimusicupdater.kt`): all GitHub API / release / nightly.link URLs repointed to `i7rvn/Spoty`
6. CI workflows (`nightly.yml`, `release.yml`, `build.yml`, `build_pr.yml`):
   - artifact renamed `vivi-music-gms-nightly` → `spoty-gms-nightly`
   - release asset `vivi.apk` → `spoty.apk`
   - banner/changelog image URLs repointed to `i7rvn/Spoty`

## NOT done — you have to do these yourself

1. **App icon / mipmap assets** — I have no image-generation tool that outputs Android launcher
   icons (adaptive icon layers, all mipmap-* densities). This needs real PNG/vector assets you
   supply or design. Nothing was touched under `mipmap-*`.
2. **MainActivity.kt OLED true-black default** — not changed. The doc called this "optional"; I
   didn't touch theme defaults because I don't know which flag/const controls it in this codebase
   without you confirming intent. Speculation, not fact, so I left it alone.
3. **Compilation is unverified.** I have no Android SDK / Gradle / emulator in this environment,
   and no network access to Google's Maven or AGP repos (network is allowlisted to a short list of
   domains — Google's repos aren't on it). This zip has never been built. Likely there are zero
   compile errors from the rename itself (sed-based package rename is mechanical and low-risk),
   but "likely" is not "confirmed" — build it yourself before you trust it.
4. **Signing/CI secrets** (`KEYSTORE`, `KEY_ALIAS`, `KEYSTORE_PASSWORD`, `KEY_PASSWORD`,
   `LASTFM_API_KEY`, `LASTFM_SECRET`) — still need to be added to your GitHub repo secrets, as
   covered earlier in this conversation. Not something a zip file can contain.
5. **README / CONTRIBUTING / issue templates** — not rewritten in this pass. Still say VIVI in
   places. Say so if you want those rewritten too.

## Second-pass fixes (bugs found and corrected after the first zip was delivered)

- **Bug found:** the first pass only moved `app/src/main/kotlin/com/music/vivi`. Eight other
  source sets still had the literal `com/music/vivi` directory on disk with mismatched package
  text inside: `app/src/gms`, `app/src/foss`, `applecanvas`, `canvas/main`, `canvas/test`,
  `betterlyrics`, `vivimusiccanvas`, `artistvideo`. All eight are now moved to `com/infinity/spoty`.
- **Bug found:** a blind `vivimusic` → `updater_lib` substring replace corrupted
  `com.infinity.spoty.vivimusiccanvas` into `.updater_libcanvas` (substring collision) in 3 files.
  Reverted back to `vivimusiccanvas`.
- Renamed `ViviMusicLyrics.kt`, `ViviNotificationProvider.kt`, `ViviPrefCache.kt` (+ their class
  refs everywhere) to `SpotyLyrics.kt` / `SpotyNotificationProvider.kt` / `SpotyPrefCache.kt`.
- Removed a stray junk file (`et --hard 33c82f9`) that was a leftover git-log dump from upstream.

## Still NOT done — deliberately, not an oversight

`vivimusic` still appears ~440+ times as: the Gradle **root project name** (`settings.gradle.kts:
rootProject.name = "vivimusic"`), a Gradle **module name** (`:vivimusiccanvas`), function names
(`vivimusicTheme()`), and constant names (`vivimusic_AUTO_SCROLL_DURATION`, etc.) scattered across
dozens of files. I stopped here on purpose: renaming a Gradle module means renaming its directory,
its `include()` line, and every `project(":vivimusiccanvas")` reference in every build.gradle.kts
that depends on it, consistently, with no compiler available to catch a mistake. The bug above
(the `updater_lib`/`vivimusiccanvas` collision) is proof that blind text substitution at this depth
produces silent breakage. Doing this correctly requires either a real build loop to verify each
step, or you doing it by hand in Android Studio where the IDE renames symbols and their usages
together.

## Confidence labels

- Confirmed: 0 remaining `com.music.vivi` / `com.vivi.vivimusic` string matches anywhere in the tree.
- Confirmed: applicationId, namespace, app_name.xml, and the listed links match the target values.
- Likely: the app still compiles after this rename (mechanical rename, but unverified — see above).
- Speculation: any icon, theme-default, or README work is unresolved — flagged above, not silently done.
