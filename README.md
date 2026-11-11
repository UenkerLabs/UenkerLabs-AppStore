# UenkerLabs AppStore

Open-source Android app source collection and distribution website.

**Source:** https://github.com/YZUe1980/UenkerLabs-AppStore
**Website target:** https://yzue1980.github.io/UenkerLabs-AppStore/
**Signed F-Droid source:** https://yzue1980.github.io/UenkerLabs-AppStore/repo
**Repository certificate SHA-256:** `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`

## Current status (2026-11-11)

- The public `main` branch contains the website and **development source code**, not released APKs.
- Android prototypes: calendar, SMS viewer, phone helper and an alarm clock with five widgets. See [android/README.md](android/README.md).
- The `gh-pages` branch contains a **new independently signed, currently empty** F-Droid index and a human-readable `repo/index.html`.
- Local index JAR signatures were verified, as were the F-Droid v2 JSON SHA-256 and icon digest. The public HTTPS response and an actual Android F-Droid client installation/update have **not** been verified.
- **Zero installable applications are currently published.** Do not represent a source tree as an installed APK.

## Distribution identity

This is a new signing certificate, **not** a silent rename of a previous repository certificate. Clients trusting an older certificate cannot automatically switch to the new source. The old published index was removed from the current `gh-pages` tree, but can remain in historical Git commits.

The private signing key and password are **not** in GitHub. Subsequent index releases must use the same signing identity; loss of the key means losing secure continuity. Back up the key securely before publishing a first APK.

## Project requirements

- Original branding and art, and license-compliant use of external source code.
- Privacy first, no unnecessary permissions or tracking.
- Android builds and tests before publishing, especially alarms, SMS, phone calls and call screening.
- Source review for credentials and personal data.
- No unsolicited or prohibited in-car radar alert functions.
- No automated publication of unreviewed APKs.

## Repository structure

- `index.html`, `assets/`, `favicon.svg`: public website source.
- `android/`: native Android prototypes.
- `docs/ARCHITECTURE.md`: source-to-signed-F-Droid release process.
- `docs/ANDROID_REPLACEMENT_ROADMAP.md`: app replacement project backlog.
- `gh-pages/repo/` (branch, not directory on main): signed F-Droid metadata.

## License

Website and repository documentation: Apache-2.0 (see [LICENSE](LICENSE)).
App-specific licenses and third-party notices must be reviewed and declared before distribution.
