@echo off
REM Scaffolds a NEW sibling mod project: Xiaoxiang Realm Expansion
REM Copies only the reusable Forge/Gradle/Mixin plumbing from your
REM XiaoxiangConfigMod project (gradle wrapper, gradlew, settings.gradle,
REM the original mod's compile-only jar, license files) into a fresh
REM folder next to it. It does NOT copy any of Config Extension's own
REM source code, build output, or git history - this is a clean new project.
REM
REM After this runs, tell Claude and it'll fill in the new project's own
REM build.gradle, gradle.properties, mods.toml, and source files.

set SRC=C:\Users\YoungLorde\Desktop\XiaoxiangConfigMod
set DEST=C:\Users\YoungLorde\Desktop\XiaoxiangRealmExpansion

echo == Creating project folders ==
mkdir "%DEST%"
mkdir "%DEST%\src\main\java\com\xiaoxiang\realmexpansion"
mkdir "%DEST%\src\main\java\com\xiaoxiang\realmexpansion\mixin"
mkdir "%DEST%\src\main\java\com\xiaoxiang\realmexpansion\config"
mkdir "%DEST%\src\main\resources\META-INF"
mkdir "%DEST%\libs"

echo == Copying Gradle wrapper (lets you build with the same toolchain) ==
robocopy "%SRC%\gradle" "%DEST%\gradle" /E

echo == Copying gradlew scripts and settings.gradle ==
copy /Y "%SRC%\gradlew" "%DEST%\gradlew"
copy /Y "%SRC%\gradlew.bat" "%DEST%\gradlew.bat"
copy /Y "%SRC%\settings.gradle" "%DEST%\settings.gradle"

echo == Copying the original mod jar (needed to compile against, never bundled) ==
copy /Y "%SRC%\libs\xiaoxiang_cultivation-0.1.1302.jar" "%DEST%\libs\xiaoxiang_cultivation-0.1.1302.jar"

echo == Copying the built Config Extension jar (so this mod can register its config with it) ==
copy /Y "%SRC%\build\libs\xiaoxiang_config_ext-1.0.0.jar" "%DEST%\libs\xiaoxiang_config_ext-1.0.0.jar"

echo == Copying license and pack files ==
copy /Y "%SRC%\LICENSE" "%DEST%\LICENSE"
copy /Y "%SRC%\.gitattributes" "%DEST%\.gitattributes"
copy /Y "%SRC%\src\main\resources\pack.mcmeta" "%DEST%\src\main\resources\pack.mcmeta"

echo.
echo Done. New project folder created at:
echo   %DEST%
echo.
echo This is just the plumbing - build.gradle, gradle.properties, mods.toml,
echo the mixin config, and the actual mod source still need to be added.
echo Let Claude know it's done and it'll add those next.
pause
