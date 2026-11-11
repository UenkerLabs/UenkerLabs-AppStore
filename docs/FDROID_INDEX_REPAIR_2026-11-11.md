# Historischer F-Droid-Indexfehler (11.11.2026) – nicht aktueller Paketstatus

**Historischer Nachweis:** Der folgende Bericht dokumentiert den anfänglichen Zustand, als der Katalog tatsächlich null Apps enthielt. **Aktuell sind fünf Beta-Apps veröffentlicht**; die kanonische URL lautet https://uenkerlabs.github.io/UenkerLabs-AppStore/repo . Nicht als aktuelle Betriebsanleitung verwenden.

---

# F-Droid index format correction (historical 2026-11-11)

The previous signed v2 index entry was missing the mandatory `index.numPackages` field. The official F-Droid client v2 model requires this value and rejects documents without it in its unit tests. The existing repository has 0 packages, so the corrected value is 0.

The `gh-pages` branch contains an updated `repo/entry.json` and a newly signed `repo/entry.jar`. The certificate was reused, not regenerated, preserving the existing F-Droid trust fingerprint.

Verification now checks the required field, index count, entry/index timestamp, SHA256 digest, JAR signature, certificate fingerprint, and that signed `entry.json` content matches the separately hosted file. The Pages workflow deploys the corrected source. A successful public test is not an Android installation test.

Sources:
- https://github.com/f-droid/fdroidclient/blob/master/libs/index/src/commonMain/kotlin/org/fdroid/index/v2/IndexV2.kt
- https://github.com/f-droid/fdroidclient/blob/master/libs/index/src/commonTest/kotlin/org/fdroid/index/v2/EntryTest.kt
- https://github.com/f-droid/fdroidserver/blob/master/fdroidserver/index.py
