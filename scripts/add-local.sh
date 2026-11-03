#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
# Sign a locally built, unsigned release APK with the Tangelo OS app key and
# put it into repo/.
# Usage: scripts/add-local.sh path/to/app-release-unsigned.apk
# KEYS_DIR (default ~/.config/tangelo-apps-keys) holds
# app-keystore.p12 and secrets.env.
set -euo pipefail

root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
apk=${1:?Usage: $0 path/to/unsigned.apk}
keys=${KEYS_DIR:-$HOME/.config/tangelo-apps-keys}
sdk=${ANDROID_HOME:-$HOME/Android/Sdk}
bt=$(ls -d "$sdk"/build-tools/* | sort -V | tail -n 1)
set -a; . "$keys/secrets.env"; set +a

work=$(mktemp -d "$root/.add-local.XXXXXX")
trap 'rm -rf -- "$work"' EXIT
"$bt/zipalign" -f -p 4 "$apk" "$work/aligned.apk"
"$bt/apksigner" sign --ks "$keys/app-keystore.p12" --ks-key-alias tangelo-safeplace \
    --ks-pass env:TANGELO_APP_KEYSTORE_PASS --out "$work/signed.apk" "$work/aligned.apk"
read -r package code < <("$bt/aapt2" dump badging "$work/signed.apk" |
    sed -n "1s/^package: name='\([^']*\)' versionCode='\([0-9]*\)'.*/\1 \2/p")
mkdir -p "$root/repo"
install -m 0644 "$work/signed.apk" "$root/repo/${package}_${code}.apk"
echo "Added repo/${package}_${code}.apk; run scripts/fetch.py --check and scripts/publish.sh."
