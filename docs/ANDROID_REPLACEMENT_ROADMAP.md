# UenkerLabs Android replacement roadmap — 2026-11-10

Device focus: Nothing Phone 3a Pro, with general Android 8.0+ compatibility as a starting hypothesis. No claim of device compatibility before testing.

| Area | Current prototype | Migration strategy | Risk |
|---|---|---|---|
| Calendar | Source started | Read Android Calendar Provider; kSync for Infomaniak CalDAV; Outlook app native calendar sync; Google account via Android | Medium: permissions, recurring events, sync and time zones |
| SMS | Read-only SMS source started | Local transparent spam labels and manual blocklist; proper default SMS app later | High: Android SMS role, MMS, RCS limitations |
| Phone | Dial-pad + optional call screening source started | Keep system dialer until complete InCallService and emergency-call testing | Critical: call reliability, emergency calls, role requirements |
| Contacts | Planning | ContactsContract UI, rely on kSync/CardDAV and existing sync adapters | Medium: merges, duplicates, edits |
| Tasks | Planning | Local-first tasks; CalDAV VTODO via compatible sync bridge, not assumed available via Calendar Provider | Medium |
| Notes | Planning | Local-first searchable text, optional explicit export/sync | Low-medium |
| Files | Planning | Android Storage Access Framework, document tree, privacy-first | Medium |
| Gallery/photos | Planning | MediaStore/Photo Picker with permission minimization | Medium |
| Clock/alarms | Planning | AlarmClock intents first, real reliable alarm engine later | High |
| Calculator | Planning | Fully offline, accessible | Low |
| Weather | Planning | Provider and privacy/cost decision before network calls | Medium |
| Browser/search | Evaluate established FOSS apps first | Building a browser engine anew is economically unsound | High |
| Keyboard | Evaluate established FOSS IMEs | Sensitive key input, accessibility, multilingual support | High |
| Maps/navigation | Evaluate Organic Maps / other maintained FOSS solutions | Real-time routing and map data are substantial infrastructure | High |
| Camera | Keep existing OEM camera for now | Hardware/vendor-specific image quality and camera APIs | High |
| App store | Existing UenkerLabs-AppStore website + legacy index | Signed and validated F-Droid metadata before claiming distribution | High |

## Approach
1. Ship offline game first to prove signing and APK delivery without personal data.
2. QA calendar on a non-primary test calendar in each account; compare round-trips.
3. SMS and phone remain supplementary previews until proper system role and regression coverage.
4. Replace Google components **gradually**, without removing Android system services or Google Play Services blindly.
5. Prefer maintained open-source integrations to rewriting complex protocols where appropriate.
6. Collect device test evidence before labeling anything released.

## Explicit non-goals for current prototypes
- No silent spam deletion.
- No phone number harvesting, remote call scoring or uploaded SMS.
- No false claim of RCS compatibility.
- No OAuth token collection or direct Google/Microsoft login in calendar v0.1.
- No mass replacement of default apps.

