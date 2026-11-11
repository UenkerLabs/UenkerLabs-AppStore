#!/usr/bin/env python3
"""Read-only online transport and signature check for the public F-Droid source.

Requires Python 3, a JDK with jarsigner/keytool, and public internet access.
This does not check Android client compatibility or install an APK.
"""
import hashlib
import zipfile
import io
import argparse
import json
from pathlib import Path
import re
import subprocess
import tempfile
from urllib.request import Request, urlopen

BASE = "https://yzue1980.github.io/UenkerLabs-AppStore/repo"
EXPECTED_CERT_SHA256 = "1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8"
LOCAL_ROOT = None

def fetch(relative):
    if LOCAL_ROOT is not None:
        return (LOCAL_ROOT / relative).read_bytes()
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
    entry_blob = fetch("entry.json")
    entry = json.loads(entry_blob)
    required = ("name", "sha256", "size", "numPackages")
    for field in required:
        if field not in entry["index"]:
            raise ValueError("F-Droid v2 entry index missing required field: " + field)
    if type(entry["index"]["numPackages"]) is not int or entry["index"]["numPackages"] < 0:
        raise ValueError("numPackages must be a nonnegative integer")
    if entry["index"]["name"].lstrip("/") != "index-v2.json":
        raise ValueError("Incorrect F-Droid index path")
    v2_blob = fetch("index-v2.json")
    if len(v2_blob) != entry["index"]["size"]:
        raise RuntimeError("F-Droid v2 index size mismatch")
    if sha(v2_blob) != entry["index"]["sha256"]:
        raise RuntimeError("F-Droid v2 index SHA-256 mismatch")
    index = json.loads(v2_blob)
    if entry["index"]["numPackages"] != len(index.get("packages", {})):
        raise ValueError("numPackages does not match the number of packages")
    if entry["timestamp"] != index["repo"]["timestamp"]:
        raise ValueError("Repository entry/index timestamp mismatch")
    icon_meta = index["repo"]["icon"]["en-US"]
    icon_data = fetch(icon_meta["name"].lstrip("/"))
    if len(icon_data) != icon_meta["size"] or sha(icon_data) != icon_meta["sha256"]:
        raise RuntimeError("F-Droid icon size or SHA-256 mismatch")
    v1 = json.loads(fetch("index-v1.json"))
    if v1["repo"]["address"].rstrip("/") != BASE or index["repo"]["address"].rstrip("/") != BASE:
        raise RuntimeError("Repository URLs mismatch")
    with tempfile.TemporaryDirectory() as temp:
        entry_jar = fetch("entry.jar")
        check_signed_jar("entry.jar", entry_jar, temp)
        with zipfile.ZipFile(io.BytesIO(entry_jar)) as signed_zip:
            if signed_zip.read("entry.json") != entry_blob:
                raise ValueError("Signed entry.jar differs from hosted entry.json")
        index1_jar = fetch("index-v1.jar")
        check_signed_jar("index-v1.jar", index1_jar, temp)
        with zipfile.ZipFile(io.BytesIO(index1_jar)) as signed_zip:
            if signed_zip.read("index-v1.json") != fetch("index-v1.json"):
                raise ValueError("Signed index-v1.jar differs from hosted v1 JSON")
    packages = index.get("packages", {})
    count = len(packages)
    if count == 0:
        raise ValueError("Refuse to publish an empty beta repository")
    if len(v1.get("apps", [])) != count or len(v1.get("packages", {})) != count:
        raise ValueError("F-Droid v1 and v2 package counts differ")
    expected_apps = {"com.uenkerlabs.calendar", "com.uenkerlabs.alarm", "com.uenkerlabs.arcade"}
    if set(packages) != expected_apps:
        raise ValueError("Unexpected package set: expected Calendar, Alarm and Arcade")
    if set(v1["packages"]) != expected_apps or {a["packageName"] for a in v1["apps"]} != expected_apps:
        raise ValueError("v1 and v2 package sets differ")
    calendar_versions = [v["manifest"]["versionCode"] for v in packages["com.uenkerlabs.calendar"]["versions"].values()]
    if sorted(calendar_versions) != [1, 2]:
        raise ValueError("Expected Calendar versions 1 and 2")
    expected_app_signer = "d903bd3c7f9bb4fc9738b86982b9064ee9e493c47dc0c2f587b5c7828ea25d30"
    for appid, package in packages.items():
        for version in package.get("versions", {}).values():
            f = version["file"]
            data = fetch(f["name"].lstrip("/"))
            if len(data) != f["size"] or sha(data) != f["sha256"]:
                raise ValueError("Bad APK size or SHA-256: " + f["name"])
            signatures = version["manifest"]["signer"]["sha256"]
            if signatures != [expected_app_signer]:
                raise ValueError("Unexpected app signing identity: " + appid)
    print(f"PASS: F-Droid v1+v2 schemas, HTTPS, both JAR signatures, APK hashes and stable app signer. Packages: {count}")
    print("NOTE: Android device installation and in-place updates still need user beta testing.")

if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("--repo-dir", type=Path, default=None)
    LOCAL_ROOT = p.parse_args().repo_dir
    main()
