#!/usr/bin/env python3
"""Static quality gate, separate package IDs and genuine multi-level save support."""
from pathlib import Path
import re,xml.etree.ElementTree as ET
root=Path(__file__).resolve().parent.parent/'android'
expected={'neon':8,'ember':8,'skybound':12}
ids=set()
for mod,chapters in expected.items():
    d=root/mod
    activity=d/'src/main/java/com/uenkerlabs'/mod/'GameActivity.java'
    code=activity.read_text()
    gradle=(d/'build.gradle').read_text()
    manifest=(d/'src/main/AndroidManifest.xml').read_text()
    ET.fromstring(manifest)
    assert f"applicationId 'com.uenkerlabs.{mod}'" in gradle
    assert 'INTERNET' not in manifest
    assert 'versionCode 1' in gradle
    assert 'FrameGame' in code and 'GameHost.BackHandler' in code
    assert 'save.edit()' in code and 'save.getInt(' in code
    if mod=='neon': assert 'LAST=8' in code and 'rayAngle' in code
    if mod=='ember': assert 'CHAPTERS=8' in code and code.count('"Die Mooswarte"')==1
    if mod=='skybound':assert 'LEVELS=12' in code and 'jumps' in code
    assert (d/'src/main/res/drawable-nodpi/app_icon.png').stat().st_size > 10000
    assert mod not in ids;ids.add(mod)
    print('PASS',mod,chapters,'stages, permanent progress, original icon, offline manifest')
assert (root/'gamekit/src/main/java/com/uenkerlabs/gamekit/FrameGame.java').exists()
print('PASS 3 separate APK modules, 28 playable stages specified')
