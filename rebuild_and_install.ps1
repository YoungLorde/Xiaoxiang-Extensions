# rebuild_and_install.ps1
# One-click build + install for xiaoxiang_config_ext.
# Run this from PowerShell after Claude (or you) edit source files -
# it builds the mod and drops the finished jar straight into your mods folder.
#
# Usage:  .\rebuild_and_install.ps1   (or just double-click rebuild_and_install.bat)
#
# Everything printed to this window is ALSO saved to build_log.txt in this
# project folder, and the window now stays open (press Enter to close) no
# matter what happens - success, failure, or a totally unexpected script
# error - so a failed build's error output can never disappear before you
# get a chance to read or copy it.

$ErrorActionPreference = "Stop"

$ProjectDir = "C:\Users\YoungLorde\Desktop\XiaoxiangConfigMod"
$ModsDir    = "C:\Users\YoungLorde\AppData\Roaming\ATLauncher\instances\XiaoCultivationWorld\mods"
# Single source of truth for the version number this script cares about -
# JarName and the versions\ archive folder below both derive from this one
# value, so bumping a release only means changing this ONE line here (plus
# mod_version in gradle.properties, which is what the build itself reads).
$Version    = "1.0.9"
$JarName    = "xiaoxiang_config_ext-$Version.jar"
$LogFile    = Join-Path $ProjectDir "build_log.txt"
# Same courtesy copy rebuild_and_install.bat makes for Realm Expansion's
# compile-time dependency, kept in sync here so either script does the same job.
$RealmExpansionLibs = Join-Path $ProjectDir "..\XiaoxiangRealmExpansion\libs"
# Every successful build also gets archived here, one folder per version, so
# past releases stay on hand for reference without digging through build\
# (which gradlew clean wipes on every run).
$VersionsDir = Join-Path $ProjectDir "versions\$Version"

try {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

    Write-Host "==> Building $JarName ..." -ForegroundColor Cyan
    Write-Host "==> Full output is also being saved to: $LogFile" -ForegroundColor Cyan
    Set-Location $ProjectDir

    # Tee everything Gradle prints (normal output AND errors, via 2>&1) to the
    # log file AND this window at the same time, so nothing is ever lost even
    # if the window gets closed before you finish reading it.
    #
    # IMPORTANT: PowerShell wraps every line an external program writes to its
    # error stream (stderr) as an ErrorRecord once it's merged in via 2>&1 -
    # and with $ErrorActionPreference = "Stop" (set above), the FIRST such
    # line throws and aborts the whole script immediately, well before Gradle
    # is actually done. javac/Mixin write plain informational "Note:" lines to
    # stderr, not real errors, so this was killing the build after only a few
    # lines of output and hiding the real compile error entirely. Switching to
    # "Continue" just for this one call fixes that: stderr lines still print
    # and still get logged, they just stop being treated as fatal.
    $prevErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & .\gradlew.bat clean build 2>&1 | Tee-Object -FilePath $LogFile
    $gradleExit = $LASTEXITCODE
    $ErrorActionPreference = $prevErrorActionPreference

    if ($gradleExit -ne 0) {
        Write-Host ""
        Write-Host "==> BUILD FAILED (exit code $gradleExit). Nothing was copied to mods\." -ForegroundColor Red
        Write-Host "==> Full log saved to: $LogFile" -ForegroundColor Red
        exit $gradleExit
    }

    $builtJar = Join-Path $ProjectDir "build\libs\$JarName"
    if (-not (Test-Path $builtJar)) {
        Write-Host "==> Build succeeded but expected jar not found at: $builtJar" -ForegroundColor Red
        Write-Host "==> If you just bumped mod_version in gradle.properties, edit `$Version" -ForegroundColor Yellow
        Write-Host "==> near the top of this file to match the new version number - this is the" -ForegroundColor Yellow
        Write-Host "==> exact trap that made a rebuild silently install nothing before." -ForegroundColor Yellow
        exit 1
    }

    Write-Host "==> Build succeeded. Installing to mods folder ..." -ForegroundColor Cyan
    Copy-Item -Path $builtJar -Destination (Join-Path $ModsDir $JarName) -Force

    if (Test-Path $RealmExpansionLibs) {
        Write-Host "==> Also refreshing Realm Expansion's compile-time copy of this jar ..." -ForegroundColor Cyan
        Copy-Item -Path $builtJar -Destination (Join-Path $RealmExpansionLibs $JarName) -Force
    }

    Write-Host "==> Archiving a copy into versions\$Version\ for future reference ..." -ForegroundColor Cyan
    if (-not (Test-Path $VersionsDir)) { New-Item -ItemType Directory -Path $VersionsDir -Force | Out-Null }
    Copy-Item -Path $builtJar -Destination (Join-Path $VersionsDir $JarName) -Force

    Write-Host "==> Done. $JarName is installed. Fully restart Minecraft to pick it up." -ForegroundColor Green
}
catch {
    Write-Host ""
    Write-Host "==> SCRIPT ERROR: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.ScriptStackTrace -ForegroundColor Red
}
finally {
    Write-Host ""
    Read-Host "Press Enter to close this window"
}
