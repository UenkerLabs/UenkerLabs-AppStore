#!/usr/bin/env python3
"""Read-only online transport and signature check for the public F-Droid source.

Requires Python 3, a JDK with jarsigner/keytool, and public internet access.
This does not check Android client compatibility or install an APK.
"""
import hashlib
import json
from pathlib import Path
import re
import subprocess
import tempfile
from urllib.request import Request, urlopen

BASE = "https://yzue1980.github.io/UenkerLabs-AppStore/repo"
EXPECTED_CERT_SHA256 = "1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8"

def fetch(relative):
    url = BASE + "/" + relative
    with urlopen(Request(url, headers={"User-Agent": "UenkerLabs-FDroid-Check/1.0"}), timeout=20) as response:
        if response.status != 200:
            raise RuntimeError(f"Expected HTTP 200 for {url}, got {response.status}")
        return response.read()

def sha(data):
    return hashlib.sha256(data).hexdigest()

def check_signed_jar(name, blob, directory):
    filename = Path(directory) / name
    filename.write_bytes(blob)
    verification = subprocess.run(["jarsigner", "-verify", "-verbose", "-certs", str(filename)],
        capture_output=True, text=True, check=True)
    if "jar verified." not in verification.stdout.lower():
        raise RuntimeError(f"Jar has no verified signature: {name}")
    cert = subprocess.run(["keytool", "-printcert", "-jarfile", str(filename)],
        capture_output=True, text=True, check=True)
    match = re.search(r"SHA256:\s*([A-Fa-f0-9:]+)", cert.stdout)
    if not match:
        raise RuntimeError(f"Cannot find signing certificate fingerprint: {name}")
    fp = match.group(1).replace(":", "").upper()
    if fp != EXPECTED_CERT_SHA256:
        raise RuntimeError(f"Unexpected signer fingerprint for {name}: {fp}")

def main():
    entry = json.loads(fetch("entry.json"))
    v2_blob = fetch("index-v2.json")
    if len(v2_blob) != entry["index"]["size"]:
        raise RuntimeError("F-Droid v2 index size mismatch")
    if sha(v2_blob) != entry["index"]["sha256"]:
        raise RuntimeError("F-Droid v2 index SHA-256 mismatch")
    index = json.loads(v2_blob)
    icon_meta = index["repo"]["icon"]["en-US"]
    icon_data = fetch(icon_meta["name"].lstrip("/"))
    if len(icon_data) != icon_meta["size"] or sha(icon_data) != icon_meta["sha256"]:
        raise RuntimeError("F-Droid icon size or SHA-256 mismatch")
    v1 = json.loads(fetch("index-v1.json"))
    if v1["repo"]["address"].rstrip("/") != BASE or index["repo"]["address"].rstrip("/") != BASE:
        raise RuntimeError("Repository URLs mismatch")
    with tempfile.TemporaryDirectory() as temp:
        check_signed_jar("entry.jar", fetch("entry.jar"), temp)
        check_signed_jar("index-v1.jar", fetch("index-v1.jar"), temp)
    count = len(index.get("packages", {}))
    print(f"PASS: public HTTPS, index hashes, icon and both signed JARs. Package IDs: {count}")
    if count == 0:
        print("WARN: No APKs published; nothing can be installed from this source yet.")

if __name__ == "__main__":
    main()
