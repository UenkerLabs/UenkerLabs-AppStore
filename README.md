# UenkerLabs AppStore

Independent, open-source Android app source collection and F-Droid distribution project.

- **Source:** https://github.com/YZUe1980/UenkerLabs-AppStore
- **Website (target):** https://yzue1980.github.io/UenkerLabs-AppStore/
- **Signed F-Droid beta source (five APKs):** https://yzue1980.github.io/UenkerLabs-AppStore/repo
- **Repository certificate SHA-256:** `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`

## Verified status (2026-11-11)

The default `main` branch holds **source code**. Five release-variant beta APKs are signed and indexed in the separate `gh-pages` distribution branch:
1. `calendar` — native calendar viewer/editor using existing Android calendars (Google, Infomaniak/kSync, Outlook through compatible sync integrations).
2. `messages` — SMS inbox preview with local labels and manual blocklist; no MMS/RCS/default SMS role yet.
3. `phone` — dial pad, call-log viewer, and optional local CallScreeningService; not a full default dialer.
4. `alarm` — alarm scheduling, ringtone service and **five home-screen widgets**.
5. `arcade` — **five original offline mini-games** with no network permission.

The universal mail client, privacy browser and lawful driving assistant are design backlog, **not coded releases**.

The `gh-pages` branch contains five **beta APKs** signed with the separate UenkerLabs app signing certificate (SHA-256 `D903BD3C7F9BB4FC9738B86982B9064EE9E493C47DC0C2F587B5C7828EA25D30`). The official `fdroidserver` generated the v1/v2 indexes; the existing F-Droid repository signing identity was preserved. All five Android release builds, APK v2/v3 signatures, index JAR signatures and APK hashes were verified. Public delivery must pass GitHub Pages deployment checks; Android-client install and in-place update are **not yet tested**. These are early-access prototypes, not finished system-app replacements.

## Quality and release

- Review original code, privacy permissions, accessibility and third-party license obligations.
- `bash scripts/check-source.sh`: tracked-source audit and pure Java sliding-puzzle smoke test when JDK exists. Not a substitute for Android builds or tests.
- Android builds require JDK 17, SDK 35, AGP 8.8.2, Gradle 8.10.2; see [android/README.md](android/README.md).
- Do not sign or release an untested app. Never commit private keys, passwords or user data.
- Private key backup custody must remain restricted and independently checked before each publication.
- An existing prior repository certificate is not silently interchangeable with the current certificate.

## Layout

- `android/` — native Android modules.
- `scripts/` — optional local audit and smoke test.
- `docs/ARCHITECTURE.md` — F-Droid signing and release checks.
- `docs/ANDROID_REPLACEMENT_ROADMAP.md` — feature roadmap and migration risks.
- `index.html`, `assets/`, `favicon.svg` — website.
- `gh-pages` branch `repo/` — signed metadata and five beta APKs.

## License
Site source and repository documentation: Apache-2.0 (root [LICENSE](LICENSE)).
App sources are original prototypes; verify and declare per-app licensing before distribution.
