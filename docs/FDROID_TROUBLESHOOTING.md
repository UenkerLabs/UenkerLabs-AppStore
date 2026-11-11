# F-Droid: invalid repository – UenkerLabs

The repository currently contains **zero installable APKs**. A source-code commit is never a published app.

## Verified
- Repository owner/name: `YZUe1980/UenkerLabs-AppStore` (public).
- Signed F-Droid metadata is on `gh-pages` at `repo/`.
- The signing certificate SHA-256 in both repository metadata and the private, backed-up PKCS#12 key is `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`.

## Not yet verified
- Actual GitHub Pages configuration, HTTPS response and Android F-Droid client acceptance.
- A first signed release, install or update.

## Recovery for "Invalid repository"
1. Open GitHub `Settings → Pages`: https://github.com/YZUe1980/UenkerLabs-AppStore/settings/pages
2. Preferred simple source: `Deploy from a branch` → `gh-pages` → `/ (root)`. Save. The branch root includes `index.html` and `.nojekyll`.
3. Alternatively: set source to `GitHub Actions`, then run `Manual F-Droid Pages deployment and verification` once. This workflow checks signed indexes locally, publishes the existing static site, then checks public HTTPS and signatures. It does not read private signing material and does not publish APKs.
4. Check https://yzue1980.github.io/UenkerLabs-AppStore/repo/entry.json in a regular browser. **A 404, network error or HTML error page is not a working F-Droid source.**
5. In F-Droid, add only the URL `https://yzue1980.github.io/UenkerLabs-AppStore/repo` in the URL field. If there is a separate fingerprint field, enter `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`. Avoid accidentally including `?fingerprint=` in the URL field when also entering it separately.
6. If the client still rejects the source even after public checks pass, capture the **exact exception text and F-Droid app version**. The missing APK is not enough evidence on its own to establish the cause.

## Real release process
Use maintained `fdroidserver` to generate future indexes from reviewed, release-signed APKs; avoid hand-editing signed jars. Protect the repository signing key and password. Preserve the same certificate identity for subsequent releases. Build, device-test, sign and verify an APK before making a first release.

References:
- https://f-droid.org/de/docs/Setup_an_F-Droid_App_Repo/
- https://f-droid.org/en/docs/All_our_APIs/
- https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site
