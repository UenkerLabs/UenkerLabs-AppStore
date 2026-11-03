<p align="center"><img src="icon.png" width="120" alt="Tangelo OS Apps icon"></p>

<h1 align="center">Tangelo OS Apps</h1>

<p align="center">The F-Droid repository of <a href="https://github.com/YZUe1980/tangelo-os">Tangelo OS</a>: apps that are not in the official F-Droid repository, signed and checked before every release.</p>

## Add the repository

Tangelo OS builds ship with this repository already enabled in F-Droid. On other devices, add it in F-Droid → Settings → Repositories:

| | |
| --- | --- |
| Address | `https://yzue1980.github.io/tangelo-apps/repo` |
| Fingerprint (SHA-256) | `DDCB5AC1B9046E2F380E7AC086E318B0528C5CFA7D26FA148ACAC399209BE05C` |

F-Droid only accepts the index if it is signed with the key behind this fingerprint.

## Apps

No apps are published yet. The repository is enabled in Tangelo OS so that apps can be added without a system update.

### Not mirrored

Proton Mail and Proton Drive publish signed APKs on GitHub, but at 160 and 199 MB they exceed GitHub's 100 MB file limit. Install them, together with Proton Pass and Proton Calendar, from Aurora Store, which is itself available in the official F-Droid repository. Proton VPN is in the official F-Droid repository too.

## How a release is made

```bash
# Needs Docker, Python 3 with PyYAML, Java and the Android SDK build tools (JAVA_HOME, ANDROID_HOME).
scripts/fetch.py                                  # new upstream releases from sources.yml
scripts/add-local.sh path/to/app-release-unsigned.apk  # apps built and signed here
scripts/publish.sh                                # signed index in repo/
scripts/publish.sh --push                         # publish to GitHub Pages
```

- `sources.yml` pins every app to its package name and signing certificate. `scripts/fetch.py` refuses any APK that differs, and fdroidserver checks the same certificate again (`AllowedAPKSigningKeys` in `metadata/`).
- Only the newest version of each app is served.
- The index is built with the official fdroidserver container, pinned by digest in `scripts/publish.sh`.
- `gh-pages` holds only the current repository, without history, so old APKs do not pile up in Git.
- The keys (`repo-keystore.p12`, `app-keystore.p12`) and their passwords (`secrets.env`) are kept outside this repository on the build machine (`KEYS_DIR`, default `~/.config/tangelo-apps-keys`). Losing the repository key means existing installations stop accepting new indexes; losing the app key means apps signed with it can no longer be updated in place.

## License

Scripts and metadata: Apache-2.0. Each app keeps its own license, shown in F-Droid.
