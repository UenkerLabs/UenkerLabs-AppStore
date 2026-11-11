#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
echo "UenkerLabs tracked-source audit"
# Current tracked files only, not historical commits.
if git ls-files | grep -Ei '(^|/)(build|\.gradle|\.idea|node_modules)/|\.(apk|aab|jks|p12|keystore)$|(^|/)\.env' ; then
  echo "ERROR: build output, signing material or private config tracked"
  exit 1
fi
if git grep -nEi -e '(tangelo[[:space:]-]*os|ithaka[[:space:]-]*os)' -- . ':!scripts/check-source.sh'; then
  echo "ERROR: obsolete branding in tracked content"
  exit 1
fi
if git grep -nE -e '-----BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY-----' -- . ':!scripts/check-source.sh'; then
  echo "ERROR: PEM private key found"
  exit 1
fi
# Prevent reintroducing links to the renamed GitHub account in current public docs.
if git grep -nEi 'github[.]com/YZUe1980' -- README.md CONTRIBUTING.md index.html android/README.md docs/ARCHITECTURE.md docs/FDROID_TROUBLESHOOTING.md ; then
  echo "ERROR: current public documentation still links to the former GitHub username"
  exit 1
fi
# Validate catalog claims against the current signed three-app publication layout.
python3 - <<'PY'
from pathlib import Path
html = Path('index.html').read_text(encoding='utf-8')
assert html.count('class="app-card"') == 3, 'Public site source must show exactly three beta apps'
for app, version in {'calendar':'0.2.0', 'alarm':'0.1.0', 'arcade':'0.1.0'}.items():
    assert f'repo/UenkerLabs-{app}-beta-{version}.apk' in html, app
for paused in ('phone', 'messages'):
    assert f'repo/UenkerLabs-{paused}-beta-' not in html, paused
print('PASS: three-app website catalog and no paused-app APK links')
PY
echo "PASS: no known obsolete brand, account link or obvious tracked signing/build artifact"
if command -v javac >/dev/null && command -v java >/dev/null; then
  temp="$(mktemp -d)"
  trap 'rm -rf "$temp"' EXIT
  javac -d "$temp" \
    android/arcade/src/main/java/com/uenkerlabs/arcade/SlidePuzzle.java \
    android/arcade/src/test/java/com/uenkerlabs/arcade/SlidePuzzleSmoke.java
  java -cp "$temp" com.uenkerlabs.arcade.SlidePuzzleSmoke
else
  echo "SKIP: JDK unavailable"
fi
echo "Reminder: this is not Android compilation or an exhaustive secret scan"
