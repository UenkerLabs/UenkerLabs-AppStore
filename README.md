# YVZ80 — Independent Android Apps

**Good apps. No compromises.**

YVZ80 is an independent, open-source initiative for Android applications of all kinds: useful everyday tools, productivity apps, creative experiments, and bigger ideas. It is **not** limited to widgets, Nothing phones, or a single manufacturer.

**Website:** https://yzue1980.github.io/yvz80-appstore/  
**Source:** https://github.com/YZUe1980/yvz80-appstore

## Current status

**Early stage · No YVZ80 Android apps released yet.** The website is live as a project introduction and future app catalog. A working F-Droid installation flow for new releases has **not** been verified.

The `gh-pages` branch still contains legacy F-Droid index files associated with a previous Tangelo OS setup. They have been retained deliberately to avoid silently breaking existing trust/signature chains. **Do not interpret the presence of those files as a current YVZ80 app release.**

## What belongs here

- The public-facing YVZ80 website and future app catalog.
- Documentation for our shared development and distribution principles.
- Later: an independently validated F-Droid repository with signed releases.

Each Android app may have its **own source repository**. The store repository is the public catalog and distribution entry point, not an obligation to put every app's source into one codebase.

## Project principles

1. **Open source:** reviewable code and clear licensing.
2. **Broad Android support:** no brand-exclusive scope unless a particular app genuinely requires specific hardware.
3. **Privacy by design:** no personal records, tokens, signing keys, or hidden credentials in public commits.
4. **Honest release states:** a concept is not an available app; an APK is not a verified F-Droid update path.
5. **Lean automation:** run validation and releases intentionally; avoid unnecessary CI consumption.
6. **Secure distribution:** protect and back up signing keys; verify APK package names and signing certificates before publication.

## Structure

- `index.html`, `assets/`, `favicon.svg` — static, responsive landing page.
- `docs/ARCHITECTURE.md` — distribution model, migration plan, and signature safety.
- `CONTRIBUTING.md` — how to participate.
- `SECURITY.md` — coordinated disclosure and sensitive-data rules.
- `gh-pages` — actual published static site and **preserved legacy** `/repo` index.

## Roadmap

- [x] Rebrand the public project website and documentation.
- [ ] Verify existing F-Droid repository identity, fingerprints, and consumers.
- [ ] Decide safe signing-key custody and build/release approvals.
- [ ] Create a tested, controlled GitHub Actions publication workflow.
- [ ] Publish a first independently tested Android app with verified metadata.

## Contributing

Ideas, accessibility improvements, fixes, and translations are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

Website source and repository documentation: [Apache License 2.0](LICENSE). Each future app declares its own license independently.
