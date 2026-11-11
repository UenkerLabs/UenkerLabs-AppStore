# UenkerLabs AppStore: distribution and key custody

## Present state

- Source + website: default `main` branch.
- Website + signed empty app index: `gh-pages` branch under `repo/`.
- Canonical planned F-Droid URL: `https://yzue1980.github.io/UenkerLabs-AppStore/repo`
- Repository certificate SHA-256: `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`
- Current published package count: **0**.
- Signed local files: `repo/entry.jar`, `repo/entry.json`, `repo/index-v1.jar`, `repo/index-v1.json`, `repo/index-v2.json`, `repo/icons/icon.png`.
- JAR signatures and content digests checked locally at generation; public HTTPS and client interoperability **not confirmed**.

## Signing identity

The signing identity is new, with no assumed trust continuity from any historical source. Old metadata is not part of the current published tree; Git history may still contain past revisions.

The private PKCS#12 keystore and its passphrase must **never** be committed to GitHub, logged, emailed, added to public build artifacts or shared Drive folders. Keep separate encrypted offline backups. Do not regenerate keys for ordinary updates.

## Release checklist

1. Build the Android app from reviewed source with a pinned toolchain.
2. Verify unit/UI tests and the permissions/roles required for the app.
3. Inspect package name, versionCode, signing certificate, dependencies and third-party licenses.
4. Sign the final APK under controlled key custody.
5. Generate the repository index with the same repository signing key using a maintained F-Droid indexing tool. Include accurate APK and metadata hashes.
6. Verify `entry.jar`, `index-v1.jar`, index-v2 SHA-256, icon SHA-256 and actual APK SHA-256.
7. Publish the verified artifacts atomically to the `gh-pages` repository path.
8. Fetch files from public HTTPS independently; compare bytes, hashes and signer fingerprint.
9. Add the repository in F-Droid on Android, verify initial install and signed in-place upgrade.
10. Only after these checks add an app to the public released catalog.

Never publish a debugging APK, an unsigned APK or a source-only project as an installable app.

## GitHub Pages

Pages must be configured to serve the `gh-pages` branch root. The code connector can check repository contents but cannot establish the actual Pages hosting configuration. In `repo/index.html` the human-facing instructions state explicitly that the package source is empty.
