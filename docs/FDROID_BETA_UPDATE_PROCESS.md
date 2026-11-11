# UenkerLabs AppStore – Beta Update-Prozess

Established first beta release (2026-11-11):
- 5 APKs signed with long-term UenkerLabs app signing identity (different from F-Droid repository signer).
- App package IDs `com.uenkerlabs.calendar`, `com.uenkerlabs.messages`, `com.uenkerlabs.phone`, `com.uenkerlabs.alarm`, `com.uenkerlabs.arcade`.
- `versionCode=1`, `versionName=0.1.0-dev`.
- Full signed APKs and app signing key/password are backed up privately in UenkerLabs Google Drive (never in Git).
- Repository signing PKCS#12 key/password independently backed up, preserving existing F-Droid fingerprint.
- Generated indexes from upstream fdroidserver with 5 package entries, signed offline with existing repo signing key. Source: https://github.com/f-droid/fdroidserver

For the next update:
1. Fix requested bug and increment Android `versionCode` (strictly increasing); update `versionName`.
2. Build all APK release variants in a manual/approved CI run; audit permission changes, app behavior and sensitive data.
3. Sign offline using the SAME app signing key stored privately; verify APK v2/v3 signature and signer fingerprint.
4. Stage signed APKs on the `gh-pages` repository branch, never debug APKs, without replacing old APKs unless obsolete versions are explicitly retired.
5. Run `fdroid update --nosign` using metadata and publicly available repository certificate (never upload repo key to GitHub).
6. Check package/version counts and APK SHA-256, sign `index-v1.jar` and `entry.jar` offline using SAME repository key; upload complete signed indexes together.
7. Deploy public GitHub Pages using its approved workflow and verify HTTPS integrity and client sync.
8. Test installation/update on the intended device; report issues back to development.

Android-beta warning: calendar prototype uses the system calendar provider; SMS and phone are not complete default-role replacements; alarm is not validated as the sole critical alarm source.
