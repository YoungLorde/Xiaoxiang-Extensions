@echo off
REM rebuild_and_install.bat
REM Wrapper so you never have to touch Windows' PowerShell execution policy.
REM Just run this file instead of the .ps1 directly.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0rebuild_and_install.ps1"
