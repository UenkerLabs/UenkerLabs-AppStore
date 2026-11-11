# F-Droid Domain migration – 2026-11-11

GitHub account owner has changed from YZUe1980 to UenkerLabs. GitHub Pages deployment now uses https://uenkerlabs.github.io/UenkerLabs-AppStore/ . The previous URL at yzue1980.github.io returned HTTP 404 in the live CI check. The new F-Droid repository address is https://uenkerlabs.github.io/UenkerLabs-AppStore/repo.

The repository signing certificate SHA256 stays 1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8 and all application signing certificates stay unchanged. Both signed v1 and v2 index archives, index-v2 SHA256/size and static website links were regenerated. Users with the old source must add the new source URL in F-Droid. Do not create a new signing key. The Android apps already installed with the existing app signing identity should be updatable from the newly configured source; a real Android client acceptance and update test remain to be done.

Open testing: https://github.com/UenkerLabs/UenkerLabs-AppStore/actions
