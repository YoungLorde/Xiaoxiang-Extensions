@echo off
REM Xiaoxiang Config Extension - one-click sync to GitHub
REM Double-click this file any time you want your latest changes pushed up.
REM
REM What it does, no prompts, no pauses:
REM   1. Makes sure git is initialized in this project folder
REM   2. Stages every file EXCEPT what .gitignore excludes - the original
REM      Xiaoxiang Cultivation World jar, build output, crash logs, and the
REM      old v1 backup folder. Only your mod's own project files go up.
REM   3. Commits whatever changed since the last run (no-ops cleanly if
REM      nothing changed)
REM   4. Points the repo at github.com/YoungLorde/Xiaoxiang-Extensions
REM   5. Pushes to main
REM
REM The first push may pop up a browser window asking you to log into
REM GitHub - that's normal, it's how Git verifies it's you. You never
REM type a password or token into this script.

cd /d "C:\Users\YoungLorde\Desktop\XiaoxiangConfigMod"

echo == Initializing repo ==
git init

echo == Setting commit identity for this repo only ==
git config user.name "YoungLorde"
git config user.email "Mohamed3132@gmail.com"

echo == Staging project files (respects .gitignore) ==
git add .

echo == Committing (skips cleanly if nothing changed) ==
git commit -m "Update project files"

echo == Setting branch to main ==
git branch -M main

echo == Pointing at GitHub ==
git remote remove origin >nul 2>&1
git remote add origin https://github.com/YoungLorde/Xiaoxiang-Extensions.git

echo == Pushing ==
git push -u origin main

echo.
echo Done. Check https://github.com/YoungLorde/Xiaoxiang-Extensions
pause
