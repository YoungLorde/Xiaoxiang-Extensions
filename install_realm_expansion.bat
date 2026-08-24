@echo off
REM Xiaoxiang Realm Expansion - one-click installer
REM Put this file and install_realm_expansion.ps1 in the SAME folder, then
REM double-click this .bat. That's the only step - no other setup needed.
REM
REM This creates a fully separate "XiaoxiangRealmExpansion" folder directly
REM on your Desktop (nothing shared with XiaoxiangConfigMod except copying a
REM couple of build-plumbing files from it once, at the start). Safe to
REM re-run any time - everything it does is idempotent.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0install_realm_expansion.ps1"

echo.
pause
