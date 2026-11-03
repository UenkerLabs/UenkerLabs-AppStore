#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
# Build the signed F-Droid index for repo/ and, with --push, publish repo/ to
# the gh-pages branch (served at https://yzue1980.github.io/tangelo-apps/repo).
# Usage: scripts/publish.sh [--push]
set -euo pipefail

root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
keys=${KEYS_DIR:-$HOME/.config/tangelo-apps-keys}
image=registry.gitlab.com/fdroid/docker-executable-fdroidserver@sha256:0cfa2c987c621d15af0875ab8c8b26b810a255032b844d3990eadcca03db9293
push=false
case "${1:-}" in --push) push=true ;; '') ;; *) echo "Usage: $0 [--push]" >&2; exit 2 ;; esac

cd "$root"
python3 scripts/fetch.py --check
# GitHub rejects files over 100 MB, so GitHub Pages cannot serve them.
too_big=$(find repo -maxdepth 1 -name '*.apk' -size +95M -printf '%f ')
[[ -z "$too_big" ]] || { echo "Too large for GitHub Pages (max. 95 MB): $too_big" >&2; exit 1; }
mkdir -p repo/icons
cp icon.png repo/icons/icon.png
set -a; . "$keys/secrets.env"; set +a

# fdroidserver refuses a config.yml that others can read.
chmod 0600 config.yml
docker run --rm --user "$(id -u):$(id -g)" --env HOME=/tmp \
    --env TANGELO_REPO_KEYSTORE_PASS \
    --env GIT_CONFIG_COUNT=1 --env GIT_CONFIG_KEY_0=safe.directory --env 'GIT_CONFIG_VALUE_0=*' \
    --volume "$root:/repo" --volume "$keys:/keys:ro" \
    "$image" update --pretty --use-date-from-apk

"$push" || { echo 'Index built in repo/. Run with --push to publish.'; exit 0; }

# gh-pages keeps no history, so old APKs do not accumulate in Git.
remote=$(git remote get-url origin)
site=$(mktemp -d)
trap 'rm -rf -- "$site"' EXIT
cp -r repo "$site/repo"
touch "$site/.nojekyll"
cat > "$site/index.html" <<'EOF'
<!doctype html><meta charset="utf-8"><title>Tangelo OS Apps</title>
<p>F-Droid repository of Tangelo OS: <a href="repo/">repo/</a>.
Details: <a href="https://github.com/YZUe1980/tangelo-apps">github.com/YZUe1980/tangelo-apps</a></p>
EOF
git -C "$site" init -q -b gh-pages
git -C "$site" add -A
git -C "$site" -c user.name=YZUe1980 -c user.email=YZUe1980@users.noreply.github.com \
    commit -q -m "Publish repository $(date -u +%F)"
git -C "$site" push -q --force "$remote" gh-pages
echo "Published to gh-pages."
