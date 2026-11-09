# Distribution architecture and migration notes

## Target architecture

Android source repositories → selected GitHub Actions builds → review/signing stage → F-Droid-compatible index + APKs → GitHub Pages → Android clients.

The store is a platform for **all kinds of Android apps**, not one particular phone or app category. Google Drive may be used for internal planning but is not a public package host. SourceForge is outside this project.

## What exists now

- Main branch: public website source, documentation.
- `gh-pages` branch: public website **plus** older `repo/` metadata previously published for Tangelo OS.
- No newly approved YVZ80 APK in the catalog.

## Safety-critical migration

Earlier documentation referenced `https://yzue1980.github.io/tangelo-apps/repo` and a Tangelo OS signing setup. A rename of a package repository is **not** equivalent to migrating its signing trust.

Before activating a new package source:
1. Inventory which clients, if any, use the old URL and repository certificate.
2. Inspect actual signed index contents and certificate fingerprints.
3. Decide whether preserving old identity or deliberately introducing a new identity is appropriate.
4. Store private signing keys outside public source; design strict CI permissions and release approvals.
5. Verify a test installation **and update** on Android, including package-signature continuity.
6. Only then publish a new repository address and onboarding QR code.

**No signing keys or signing passwords belong in Git, Google Drive folders shared publicly, pull-request artifacts, or workflow logs.** Key custody/backups must be decided before automation.

## Web publishing

The site is plain HTML, CSS and a tiny JavaScript file: no framework, build pipeline, analytics, or cookies. It is intentionally able to run without a package manager. The published website is hosted from `gh-pages`. The existing `repo/` directory is preserved independently; future deployment must never overwrite it accidentally.

## Publication policy

The site lists only actual releases, with a package identifier, version, signing identity, source repository, license, supported Android versions and reproducible provenance where available. In-progress ideas remain labeled as such.
