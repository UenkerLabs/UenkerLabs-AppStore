#!/usr/bin/env python3
# SPDX-License-Identifier: Apache-2.0
"""Download new upstream releases into repo/ and verify every APK there.

Usage: scripts/fetch.py [--check]

For each app in sources.yml with `github:`, the newest regular release asset is
downloaded if repo/ does not have that version yet. Every APK in repo/ (also
the local ones added by add-local.sh) must match its package's pinned signer;
older versions of a package are removed, so the repository serves one version
per app. --check only verifies, without downloading.

Needs the Android SDK build tools (aapt2, apksigner) and Java, found via
ANDROID_HOME and JAVA_HOME.
"""
from pathlib import Path
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import urllib.request

import yaml

root = Path(__file__).resolve().parents[1]
repo = root / 'repo'
sources = yaml.safe_load((root / 'sources.yml').read_text())


def build_tool(name):
    sdk = Path(os.environ.get('ANDROID_HOME', Path.home() / 'Android/Sdk'))
    found = sorted(sdk.glob(f'build-tools/*/{name}'))
    if not found:
        sys.exit(f'{name} not found under {sdk}/build-tools; set ANDROID_HOME')
    return str(found[-1])


AAPT2, APKSIGNER = build_tool('aapt2'), build_tool('apksigner')


def apk_info(path):
    out = subprocess.run([AAPT2, 'dump', 'badging', str(path)], capture_output=True,
                         text=True, check=True).stdout.splitlines()[0]
    m = re.match(r"package: name='([^']+)' versionCode='(\d+)'", out)
    if not m:
        sys.exit(f'{path.name}: cannot read package name and version code')
    certs = subprocess.run([APKSIGNER, 'verify', '--print-certs', str(path)],
                           capture_output=True, text=True)
    if certs.returncode != 0:
        sys.exit(f'{path.name}: signature does not verify\n{certs.stderr}')
    signers = re.findall(r'certificate SHA-256 digest: ([0-9a-f]{64})', certs.stdout)
    return m.group(1), int(m.group(2)), set(signers)


def verify(path):
    package, code, signers = apk_info(path)
    source = sources.get(package)
    if source is None:
        sys.exit(f'{path.name}: {package} is not listed in sources.yml')
    if signers != {source['signer']}:
        sys.exit(f'{path.name}: signer {sorted(signers)} differs from the pinned {source["signer"]}')
    return package, code


def github(url):
    req = urllib.request.Request(url, headers={'Accept': 'application/vnd.github+json'})
    token = os.environ.get('GITHUB_TOKEN')
    if token:
        req.add_header('Authorization', f'Bearer {token}')
    with urllib.request.urlopen(req, timeout=60) as r:
        return json.load(r)


def fetch(package, source):
    release = github(f"https://api.github.com/repos/{source['github']}/releases/latest")
    assets = [a for a in release['assets'] if re.fullmatch(source['asset'], a['name'])]
    if len(assets) != 1:
        sys.exit(f"{package}: expected one asset matching {source['asset']} in "
                 f"{source['github']} {release['tag_name']}, found {len(assets)}")
    asset = assets[0]
    with tempfile.TemporaryDirectory(dir=root) as tmp:
        target = Path(tmp) / asset['name']
        print(f"{package}: checking {release['tag_name']} ({asset['name']})")
        with urllib.request.urlopen(asset['browser_download_url'], timeout=600) as r, \
                target.open('wb') as f:
            shutil.copyfileobj(r, f)
        got, code = verify(target)
        if got != package:
            sys.exit(f'{asset["name"]}: package {got}, expected {package}')
        final = repo / f'{package}_{code}.apk'
        if final.exists():
            print(f'{package}: {code} already present')
            return
        shutil.move(target, final)
        print(f'{package}: added {final.name}')


def main():
    check_only = '--check' in sys.argv[1:]
    repo.mkdir(exist_ok=True)
    if not check_only:
        for package, source in sources.items():
            if 'github' in source:
                fetch(package, source)
    newest = {}
    for apk in sorted(repo.glob('*.apk')):
        package, code = verify(apk)
        newest.setdefault(package, []).append((code, apk))
    for package, versions in sorted(newest.items()):
        versions.sort()
        for _, old in versions[:-1]:
            print(f'{package}: removing older {old.name}')
            if not check_only:
                old.unlink()
        print(f'{package}: serving {versions[-1][1].name} (signer verified)')
    missing = set(sources) - set(newest)
    if missing:
        print(f'Not in repo yet: {", ".join(sorted(missing))}')


if __name__ == '__main__':
    main()
