# Contributing to UenkerLabs AppStore

This repository contains early, open-source Android experiments and a separately signed **three-app beta catalog** (Calendar, Alarm and Games). Phone and Messages remain paused in source form; their public APKs were withdrawn. **None of the beta apps has a documented physical-device release acceptance.**

## Welcome contributions

- Report reproducible accessibility, usability, reliability or compatibility issues.
- Suggest focused improvements to the Android modules, the website and the documentation.
- Submit small pull requests with a clear change description and applicable manual verification notes.

## Safety and workflow

Do not submit passwords, API tokens, personal records, screenshots containing private information, APK/repository signing keys or signing material.

Keep the project free of advertising, invasive tracking and unnecessary network calls. Avoid new dependencies or architectural changes without review. Trigger costly CI runs deliberately, not automatically for every documentation edit.

**Publishing:** changes to the signed F-Droid repository require maintaining the original signing identity, validating the complete index and performing real-client tests. A green build is not a release acceptance.

Report security-sensitive issues privately rather than in public Issues. See [SECURITY.md](SECURITY.md).
