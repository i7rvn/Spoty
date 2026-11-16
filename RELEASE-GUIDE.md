# Spoty — Release Guide

This document explains how the automated build, signing, versioning, and
GitHub Release pipeline works, and what you need to set up once before it
can run.

---

## 1. One-time setup: generate a signing keystore

If you don't already have a release keystore, create one:

```bash
keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 \
  -validity 10000 -alias spoty-release
```

You'll be asked for a keystore password and a key password (they can be the
same or different) and some identity fields (name, org, etc. — any values
work, they're not checked).

**Keep `release.keystore` somewhere safe outside the repo.** It is never
committed — `.gitignore` already excludes `*.jks` / `*.keystore` files.

Every future update **must** be signed with this same keystore (and the same
`keyAlias`), or Android will refuse to install the update over the existing
app. Back this file up.

### Converting the keystore to Base64

GitHub Secrets only store text, so the keystore file is stored Base64-encoded
and decoded again at build time (this is handled automatically by the
signing action in the workflow — you just need to produce the Base64 text
once, for the secret).

**Linux / macOS:**
```bash
base64 -w0 release.keystore > release.keystore.base64.txt
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File release.keystore.base64.txt
```

Copy the contents of `release.keystore.base64.txt` — that's what goes into
the `KEYSTORE` secret below.

---

## 2. GitHub Secrets to configure

Go to **your repo → Settings → Secrets and variables → Actions → New
repository secret**, and add:

| Secret name | Value |
|---|---|
| `KEYSTORE` | The Base64 text from step 1 |
| `KEY_ALIAS` | The alias you used (e.g. `spoty-release`) |
| `KEYSTORE_PASSWORD` | The keystore password |
| `KEY_PASSWORD` | The key password |

Optional, only needed if you use these features:

| Secret name | Needed for |
|---|---|
| `LASTFM_API_KEY` / `LASTFM_SECRET` | Last.fm scrobbling |
| `VIVI_GITHUB_CLIENT_ID` / `VIVI_GITHUB_CLIENT_SECRET` | "Sign in with GitHub" / starring the repo from inside the app |

`GITHUB_TOKEN` is provided automatically by GitHub Actions — you don't need
to create it.

The workflow validates that `KEYSTORE`, `KEY_ALIAS`, `KEYSTORE_PASSWORD` and
`KEY_PASSWORD` are all present **before** it attempts a build. If any are
missing, the run fails immediately with a clear error instead of producing
an unsigned or debug-signed APK.

---

## 3. How automatic versioning works

The workflow follows semantic versioning: `MAJOR.MINOR.PATCH`.

- **Every push to `main`** triggers a release and automatically bumps the
  **PATCH** number (e.g. `1.0.0` → `1.0.1` → `1.0.2`).
- The very first release (when no GitHub Release exists yet) is always
  `v1.0.0`.
- `versionCode` (Android's internal build number) always increases by
  exactly 1 on every release, independent of the semantic version — this is
  what Android actually requires for updates to install correctly.
- After a successful release, the workflow commits the new version numbers
  back into `app/build.gradle.kts` on `main`, so the repository always
  reflects the last released version.

### Publishing a MINOR or MAJOR release instead

Go to **Actions → Production Release Build → Run workflow**, and set
**bump** to `minor` or `major`:

- `minor` → resets PATCH to 0 and bumps MINOR (`1.2.5` → `1.3.0`)
- `major` → resets MINOR and PATCH to 0 and bumps MAJOR (`1.3.0` → `2.0.0`)

### Manually triggering a release

The same **Run workflow** button with `bump` left as `patch` does exactly
what an automatic push would do.

### Attaching an APK to an existing release

If a release already exists and you just need to (re)attach a freshly built,
signed APK to it (no new tag, no new release) — run the workflow manually
and fill in **existing_tag** with the tag name, e.g. `v1.0.0`. This never
creates a duplicate release or tag.

---

## 4. What a release contains

Every GitHub Release gets:

- `Spoty-vX.Y.Z.apk` — the main signed APK (Universal, Google-Play-services build)
- `Spoty-vX.Y.Z-foss.apk` — the FOSS build (no Google services), for IzzyOnDroid-style installs
- `changelog.json` — machine-readable changelog used by the in-app update screen
- **Source code (zip)** and **Source code (tar.gz)** — attached automatically by GitHub to every release, no action needed

Release notes are generated automatically from the commit messages since the
previous release.

---

## 5. Safety behavior

- **Concurrency guard**: only one production release can run at a time; a
  second push while a release is in progress waits instead of racing it.
- **No duplicate versions**: the workflow checks whether the computed tag
  already exists and refuses to proceed if so.
- **No silent fallback**: if signing secrets are missing or signing fails,
  the workflow stops — it never publishes a debug or unsigned APK.
- **Signature verification**: every signed APK is verified with `apksigner
  verify` before it's attached to a release.
- **Same identity forever**: `applicationId` (`com.infinity.spoty`) and the
  signing key stay fixed across releases, so updates always install cleanly
  over the previous version.

---

## 6. Downloading the app

**GitHub repo → Releases → Latest release → Assets →** tap
`Spoty-vX.Y.Z.apk`.

---

## 7. Recovering the keystore later

If you set up a new machine and need to rebuild locally with release
signing, decode the Base64 back to a file:

**Linux/macOS:**
```bash
base64 -d release.keystore.base64.txt > release.keystore
```

**Windows (PowerShell):**
```powershell
[IO.File]::WriteAllBytes("release.keystore", [Convert]::FromBase64String((Get-Content release.keystore.base64.txt)))
```

Then place it at `app/keystore/release.keystore` and set these environment
variables before running a local release build (`./gradlew
assembleUniversalGmsRelease`):

```bash
export STORE_PASSWORD="..."
export KEY_ALIAS="..."
export KEY_PASSWORD="..."
```

(Note: the local Gradle build and the CI pipeline sign independently — CI
re-signs the APK in a separate step using the `KEYSTORE_PASSWORD`/`KEY_PASSWORD`
GitHub Secrets regardless of whether the local signing config above is set,
so CI releases work even without ever touching your machine's environment
variables.)
