@echo off
REM Xiaoxiang Config Extension - one-click push to GitHub
REM Double-click this file to run it. That's the only step.
REM
REM What it does, no prompts, no pauses:
REM   1. Initializes git in this project folder (safe even if already done)
REM   2. Stages every file EXCEPT what .gitignore excludes - the original
REM      Xiaoxiang Cultivation World jar, build output, crash logs, and the
REM      old v1 backup folder. Only your mod's own project files go up.
REM   3. Commits everything as one initial commit
REM   4. Points the repo at github.com/YoungLorde/Xiaoxiang-Extensions
REM   5. Force-pushes to main, replacing the repo's current auto-generated
REM      README.md and LICENSE with your actual project
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

echo == Committing ==
git commit -m "Initial commit: Xiaoxiang Config Extension v1.0.0"

echo == Setting branch to main ==
git branch -M main

echo == Pointing at GitHub ==
git remote remove origin >nul 2>&1
git remote add origin https://github.com/YoungLorde/Xiaoxiang-Extensions.git

echo == Pushing (force, replaces the repo's starter README/LICENSE commit) ==
git push -u origin main --force

echo.
echo Done. Check https://github.com/YoungLorde/Xiaoxiang-Extensions
pause
