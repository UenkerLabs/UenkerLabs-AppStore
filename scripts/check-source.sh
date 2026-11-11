#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
echo "UenkerLabs tracked-source audit"
# Intentionally limited to tracked files in the current revision; history is a separate audit.
if git ls-files | grep -Ei '(^|/)(build|\.gradle|\.idea|node_modules)/|\.(apk|aab|jks|p12|keystore)$|(^|/)\.env' ; then
  echo "ERROR: build output, signing material or private config tracked"
  exit 1
fi
# The old product names should not appear in current public content.
if git grep -nEi '(tangelo[[:space:]-]*os|ithaka[[:space:]-]*os)' -- . ':!scripts/check-source.sh'; then
  echo "ERROR: obsolete branding in tracked content"
  exit 1
fi
if git grep -nE '-----BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY-----' -- . ':!scripts/check-source.sh'; then
  echo "ERROR: PEM private key found"
  exit 1
fi
echo "PASS: current revision has no known obsolete branding or obvious tracked key/build files"
if command -v javac >/dev/null && command -v java >/dev/null; then
  temp="$(mktemp -d)"
  trap 'rm -rf "$temp"' EXIT
  javac -d "$temp" \
    android/arcade/src/main/java/com/uenkerlabs/arcade/SlidePuzzle.java \
    android/arcade/src/test/java/com/uenkerlabs/arcade/SlidePuzzleSmoke.java
  java -cp "$temp" com.uenkerlabs.arcade.SlidePuzzleSmoke
else
  echo "SKIP: JDK unavailable; puzzle smoke test not executed"
fi
echo "Reminder: this is not Android compilation or an exhaustive secret scan"
