@echo off
REM rebuild_and_install.bat
REM One-click build + install for xiaoxiang_config_ext.
REM Run this after editing source files - it builds the mod and drops the
REM finished jar straight into your mods folder.
REM
REM Full build output is also saved to build_log.txt in this project folder,
REM and this window stays open at the end (press any key to close) no matter
REM what happens, so a failed build's output can't disappear before you get
REM a chance to read or copy it.
REM
REM IMPORTANT - run this one FIRST if you're also rebuilding Realm Expansion:
REM Realm Expansion compiles against a COPY of this mod's jar sitting in its
REM own libs\ folder (separate projects, separate jars - it doesn't read this
REM project's source directly). This script automatically refreshes that copy
REM after a successful build, so running this one before Realm Expansion's
REM own rebuild_and_install.bat keeps Realm Expansion building against
REM whatever you just changed here. If you skip this step (or run them in the
REM wrong order), Realm Expansion may fail to compile, or silently compile
REM against an OLD copy that's missing whatever you just added here.

setlocal

set "PROJECT_DIR=%~dp0"
set "MODS_DIR=C:\Users\YoungLorde\AppData\Roaming\ATLauncher\instances\XiaoCultivationWorld\mods"
REM Single source of truth for the version number this script cares about -
REM JAR_NAME and the versions\ archive folder below both derive from this one
REM value, so bumping a release only means changing this ONE line here (plus
REM mod_version in gradle.properties, which is what the build itself reads).
set "VERSION=1.0.9"
set "JAR_NAME=xiaoxiang_config_ext-%VERSION%.jar"
set "LOG_FILE=%PROJECT_DIR%build_log.txt"
REM Where Realm Expansion keeps its own compile-time copy of this mod's jar -
REM see the note above. Kept as a separate variable so it's obvious this is
REM just a courtesy copy step, not a shared build - the two mods stay fully
REM separate projects either way.
set "REALM_EXPANSION_LIBS=%PROJECT_DIR%..\XiaoxiangRealmExpansion\libs"
REM Every successful build also gets archived here, one folder per version,
REM so past releases stay on hand for reference without digging through
REM build\ (which gradlew clean wipes on every run).
set "VERSIONS_DIR=%PROJECT_DIR%versions\%VERSION%"

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%PROJECT_DIR%"

echo ==^> Building %JAR_NAME% ...
echo ==^> Full output is also being saved to: %LOG_FILE%

call gradlew.bat clean build > "%LOG_FILE%" 2>&1
set "GRADLE_EXIT=%ERRORLEVEL%"

type "%LOG_FILE%"

if not "%GRADLE_EXIT%"=="0" (
    echo.
    echo ================================================================
    echo ==^>  BUILD FAILED ^(exit code %GRADLE_EXIT%^). Nothing was copied to mods\.
    echo ==^>  The specific error line^(s^) from above, repeated here so
    echo ==^>  they don't get lost in the scrollback:
    echo ================================================================
    findstr /C:"error:" "%LOG_FILE%"
    echo ================================================================
    echo ==^> Full log saved to: %LOG_FILE%
    goto :end
)

set "BUILT_JAR=%PROJECT_DIR%build\libs\%JAR_NAME%"
if not exist "%BUILT_JAR%" (
    echo ==^> Build succeeded but expected jar not found at: %BUILT_JAR%
    echo ==^> If you just bumped mod_version in gradle.properties, edit VERSION
    echo ==^> near the top of this file to match the new version number - this
    echo ==^> is the exact trap that made the 1.0.2 rebuild silently install
    echo ==^> nothing the first time, so it's worth double-checking here.
    goto :end
)

echo ==^> Build succeeded. Installing to mods folder ...
copy /y "%BUILT_JAR%" "%MODS_DIR%\%JAR_NAME%" >nul

if exist "%REALM_EXPANSION_LIBS%" (
    echo ==^> Also refreshing Realm Expansion's compile-time copy of this jar ...
    copy /y "%BUILT_JAR%" "%REALM_EXPANSION_LIBS%\%JAR_NAME%" >nul
    echo ==^> Done: %REALM_EXPANSION_LIBS%\%JAR_NAME%
) else (
    echo ==^> ^(Realm Expansion folder not found - skipping its jar refresh, no problem if you don't have it.^)
)

echo ==^> Archiving a copy into versions\%VERSION%\ for future reference ...
if not exist "%VERSIONS_DIR%" mkdir "%VERSIONS_DIR%"
copy /y "%BUILT_JAR%" "%VERSIONS_DIR%\%JAR_NAME%" >nul
echo ==^> Done: %VERSIONS_DIR%\%JAR_NAME%

echo ==^> Done. %JAR_NAME% is installed. Fully restart Minecraft to pick it up.

:end
echo.
pause
