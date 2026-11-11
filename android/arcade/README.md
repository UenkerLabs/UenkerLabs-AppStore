# UenkerLabs Spiele — fünf originale Offline-Minispiele

Source prototype, no APK published. Android 8+ minimum; no dangerous permissions, no INTERNET, no in-app purchases, no ads or analytics. All gameplay and graphics are original simple Android UI elements, not copies of proprietary games.

| Spiel | Regeln | Ende |
|---|---|---|
| **Zahlensprung** | 10 Additions-/Subtraktionsfragen, je vier Antworten | Punktestand aus zehn |
| **Merkfeld** | 3 von 9 Feldern 1,6 Sekunden merken und wieder antippen | 5 Runden |
| **Reaktionslabor** | Warten bis grünes Feld erscheint, Reaktionszeit messen | Ergebnis in Millisekunden |
| **Musterfolge** | Vier Felder leuchten in wachsender Reihenfolge, Folge wiederholen | Bis 8 Schritte oder Fehler |
| **Schiebepuzzle** | 3×3-Zahlen durch Verschieben in Reihenfolge bringen | Gelöst, Zugzähler |

The project uses stock Android views for accessibility, large touch targets and German-language labels. A redesign with better animations, TalkBack and adaptive layouts is planned.

## Test plan
- Build `:arcade:assembleDebug` under Android SDK 35 and Gradle 8.10.2.
- Check screen rotation, very large font, TalkBack, touch targets and app recreation.
- Test randomized sliding puzzle solvability and legal moves.
- Check stale delayed callbacks when switching minigames.
- A successful on-device test has NOT been performed.
