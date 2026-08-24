# Xiaoxiang Realm Expansion - self-contained installer
# Double-click install_realm_expansion.bat (in the same folder as this script) to run.
# Safe to re-run any time - every step below overwrites/creates idempotently.

$ErrorActionPreference = "Stop"
$root = "C:\Users\YoungLorde\Desktop\XiaoxiangRealmExpansion"
$src  = "C:\Users\YoungLorde\Desktop\XiaoxiangConfigMod"

Write-Host "== Creating project folders ==" -ForegroundColor Cyan
$dirs = @(
    "$root",
    "$root\src\main\java\com\xiaoxiang\realmexpansion",
    "$root\src\main\java\com\xiaoxiang\realmexpansion\mixin",
    "$root\src\main\java\com\xiaoxiang\realmexpansion\config",
    "$root\src\main\resources\META-INF",
    "$root\libs"
)
foreach ($d in $dirs) { New-Item -ItemType Directory -Force -Path $d | Out-Null }

Write-Host "== Copying Gradle wrapper and build plumbing from XiaoxiangConfigMod ==" -ForegroundColor Cyan
Copy-Item -Path "$src\gradle" -Destination "$root\gradle" -Recurse -Force
Copy-Item -Path "$src\gradlew" -Destination "$root\gradlew" -Force
Copy-Item -Path "$src\gradlew.bat" -Destination "$root\gradlew.bat" -Force
Copy-Item -Path "$src\settings.gradle" -Destination "$root\settings.gradle" -Force
Copy-Item -Path "$src\LICENSE" -Destination "$root\LICENSE" -Force
Copy-Item -Path "$src\.gitattributes" -Destination "$root\.gitattributes" -Force
Copy-Item -Path "$src\src\main\resources\pack.mcmeta" -Destination "$root\src\main\resources\pack.mcmeta" -Force

Write-Host "== Copying compile-only jars (never bundled into the built mod) ==" -ForegroundColor Cyan
Copy-Item -Path "$src\libs\xiaoxiang_cultivation-0.1.1302.jar" -Destination "$root\libs\xiaoxiang_cultivation-0.1.1302.jar" -Force
if (Test-Path "$src\build\libs\xiaoxiang_config_ext-1.0.0.jar") {
    Copy-Item -Path "$src\build\libs\xiaoxiang_config_ext-1.0.0.jar" -Destination "$root\libs\xiaoxiang_config_ext-1.0.0.jar" -Force
} else {
    Write-Host "  (Config Extension jar not found - build XiaoxiangConfigMod first if you want that integration to compile)" -ForegroundColor Yellow
}

Write-Host "== Writing project files ==" -ForegroundColor Cyan

$gradleProperties = @'
# Sets default memory used for gradle commands. Can be overridden by user or command line properties.
# This is required to provide enough memory for the Minecraft decompilation process.
org.gradle.jvmargs=-Xmx4G
org.gradle.daemon=false


## Environment Properties

# The Minecraft version must agree with the Forge version to get a valid artifact
minecraft_version=1.20.1
# The Minecraft version range can use any release version of Minecraft as bounds.
minecraft_version_range=[1.20.1,1.21)
# The Forge version must agree with the Minecraft version to get a valid artifact
forge_version=47.4.22
# The Forge version range can use any version of Forge as bounds or match the loader version range
forge_version_range=[47,)
# The loader version range can only use the major version of Forge/FML as bounds
loader_version_range=[47,)
# The mapping channel to use for mappings.
mapping_channel=official
# The mapping version to query from the mapping channel.
mapping_version=1.20.1


## Mod Properties

# The unique mod identifier for the mod. Must be lowercase in English locale. Must fit the regex [a-z][a-z0-9_]{1,63}
mod_id=xiaoxiang_realm_expansion
# The human-readable display name for the mod.
mod_name=Xiaoxiang Realm Expansion
# The license of the mod.
mod_license=GPL-3.0
# The mod version.
mod_version=0.1.0
# The group ID for the mod.
mod_group_id=com.xiaoxiang.realmexpansion
# The authors of the mod.
mod_authors=Thy_YoungLorde
# The description of the mod.
mod_description=Adds authentic multi-layer realm progression to Xiaoxiang Cultivation World - each major realm stage broken into ten distinct layers (three Early, three Middle, three Late, one Peak), based entirely on the original mod's own realm values. Fully configurable through Xiaoxiang Config Extension.

# Mixin version
mixin_version=0.8.5

'@

[System.IO.File]::WriteAllText("$root\gradle.properties", $gradleProperties)

$buildGradle = @'
buildscript {
    repositories {
        maven { url = 'https://repo.spongepowered.org/repository/maven-public/' }
        mavenCentral()
    }
    dependencies {
        classpath 'org.spongepowered:mixingradle:0.7.38'
    }
}

plugins {
    id 'eclipse'
    id 'idea'
    id 'maven-publish'
    id 'net.minecraftforge.gradle' version '[6.0,6.2)'
}

apply plugin: 'org.spongepowered.mixin'

version = mod_version
group = mod_group_id

base {
    archivesName = mod_id
}

// Mojang ships Java 17 to end users in 1.18+, so your mod should target Java 17.
java.toolchain.languageVersion = JavaLanguageVersion.of(17)

println "Java: ${System.getProperty 'java.version'}, JVM: ${System.getProperty 'java.vm.version'} (${System.getProperty 'java.vendor'}), Arch: ${System.getProperty 'os.arch'}"

minecraft {
    mappings channel: mapping_channel, version: mapping_version

    copyIdeResources = true

    runs {
        configureEach {
            workingDirectory project.file('run')

            property 'forge.logging.markers', 'REGISTRIES'
            property 'forge.logging.console.level', 'debug'

            mods {
                "${mod_id}" {
                    source sourceSets.main
                }
            }
        }

        client {
            property 'forge.enabledGameTestNamespaces', mod_id
            property 'mixin.env.remapRefMap', 'true'
            property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
        }

        server {
            property 'forge.enabledGameTestNamespaces', mod_id
            args '--nogui'
        }

        gameTestServer {
            property 'forge.enabledGameTestNamespaces', mod_id
        }

        data {
            workingDirectory project.file('run-data')
            args '--mod', mod_id, '--all', '--output', file('src/generated/resources/'), '--existing', file('src/main/resources/')
        }
    }
}

sourceSets.main.resources { srcDir 'src/generated/resources' }

repositories {
    flatDir {
        dir 'libs'
    }
    maven {
        url = 'https://repo.spongepowered.org/repository/maven-public/'
    }

}

dependencies {
    minecraft "net.minecraftforge:forge:${minecraft_version}-${forge_version}"

    // Mixin annotation processor for refmap generation
    annotationProcessor "org.spongepowered:mixin:${mixin_version}:processor"

    // Original mod as compile-only dependency (we reference its classes but don't bundle it)
    compileOnly fg.deobf("blank:xiaoxiang_cultivation:0.1.1302")

    // Xiaoxiang Config Extension as compile-only dependency, so we can implement its
    // IXiaoxiangExpansion interface and have our config auto-register there. This mod
    // works fine without Config Extension installed at all - it's an optional integration.
    compileOnly fg.deobf("blank:xiaoxiang_config_ext:1.0.0")
}

// This block of code expands all declared replace properties in the specified resource targets.
tasks.named('processResources', ProcessResources).configure {
    var replaceProperties = [
            minecraft_version: minecraft_version, minecraft_version_range: minecraft_version_range,
            forge_version: forge_version, forge_version_range: forge_version_range,
            loader_version_range: loader_version_range,
            mod_id: mod_id, mod_name: mod_name, mod_license: mod_license, mod_version: mod_version,
            mod_authors: mod_authors, mod_description: mod_description,
    ]
    inputs.properties replaceProperties

    filesMatching(['META-INF/mods.toml', 'pack.mcmeta']) {
        expand replaceProperties + [project: project]
    }
}

tasks.named('jar', Jar).configure {
    manifest {
        attributes([
                'Specification-Title'     : mod_id,
                'Specification-Vendor'    : mod_authors,
                'Specification-Version'   : '1',
                'Implementation-Title'    : project.name,
                'Implementation-Version'  : project.jar.archiveVersion,
                'Implementation-Vendor'   : mod_authors,
                'Implementation-Timestamp': new Date().format("yyyy-MM-dd'T'HH:mm:ssZ"),
                'MixinConfigs'            : 'xiaoxiang_realm_expansion.mixins.json'
        ])
    }

    finalizedBy 'reobfJar'
}

mixin {
    add sourceSets.main, "xiaoxiang_realm_expansion.refmap.json"
    config "xiaoxiang_realm_expansion.mixins.json"
}

publishing {
    publications {
        register('mavenJava', MavenPublication) {
            artifact jar
        }
    }
    repositories {
        maven {
            url "file://${project.projectDir}/mcmodsrepo"
        }
    }
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}

'@

[System.IO.File]::WriteAllText("$root\build.gradle", $buildGradle)

$modsToml = @'
modLoader="javafml"
loaderVersion="${loader_version_range}"
license="${mod_license}"
issueTrackerURL="https://github.com/YoungLorde/Xiaoxiang-Extensions/issues"
[[mods]]
modId="${mod_id}"
version="${mod_version}"
displayName="${mod_name}"
displayURL="https://github.com/YoungLorde/Xiaoxiang-Extensions"
credits="Thanks to the Xiaoxiang Cultivation World mod team for the original mod. Every layer value in this expansion is derived directly from their own realm numbers."
authors="${mod_authors}"
description='''
Xiaoxiang Realm Expansion
==========================

Adds authentic multi-layer realm progression to Xiaoxiang Cultivation World.

In most cultivation fiction, a major realm isn't a single flat stage - it's
broken into distinct layers you climb one at a time. This mod brings that to
Xiaoxiang Cultivation World: each realm's Early / Middle / Late / Peak
sub-stages are split into ten individual layers (three Early, three Middle,
three Late, one Peak), so every breakthrough feels like real progress instead
of one long grind to the next sub-stage.

Every layer threshold is derived directly from the original mod's own real
values - nothing is invented. No files belonging to Xiaoxiang Cultivation
World are ever modified; this mod works entirely through Mixins injecting
into the original mod's compiled classes at runtime, and can be safely
removed at any time with no lasting effect on your save.

THIS RELEASE
------------
Early build - layered progression is implemented for the Qi Refining realm
only, as a testable first pass. Every other realm behaves exactly as the
base mod intends until layering is extended to them in a future update.

CONFIGURATION
-------------
If Xiaoxiang Config Extension is installed, this mod's settings appear
automatically in its config screen - no setup needed. Without it, edit
xiaoxiang_realm_expansion-common.toml directly in your config folder.
Uninstalling this mod at any time returns realm progression to the base
mod's own defaults; it does not alter Xiaoxiang Config Extension's own
settings in any way.

CREDITS
-------
Creator: Young Lorde (Thy_YoungLorde)
Built on: Xiaoxiang Cultivation World, created by the Xiaoxiang Cultivation team
Powered by: Minecraft Forge and Mixin
'''
displayTest="IGNORE_ALL_VERSION"
[[dependencies.${mod_id}]]
    modId="forge"
    mandatory=true
    versionRange="${forge_version_range}"
    ordering="NONE"
    side="BOTH"
[[dependencies.${mod_id}]]
    modId="minecraft"
    mandatory=true
    versionRange="${minecraft_version_range}"
    ordering="NONE"
    side="BOTH"
[[dependencies.${mod_id}]]
    modId="xiaoxiang_cultivation"
    mandatory=true
    versionRange="[0.1,)"
    ordering="AFTER"
    side="BOTH"
[[dependencies.${mod_id}]]
    modId="xiaoxiang_config_ext"
    mandatory=false
    versionRange="[1.0,)"
    ordering="AFTER"
    side="BOTH"

'@

[System.IO.File]::WriteAllText("$root\src\main\resources\META-INF\mods.toml", $modsToml)

$mixinsJson = @'
{
  "required": true,
  "minVersion": "0.8.5",
  "package": "com.xiaoxiang.realmexpansion.mixin",
  "compatibilityLevel": "JAVA_17",
  "refmap": "xiaoxiang_realm_expansion.refmap.json",
  "mixins": [
    "CultivationDataLayerMixin"
  ],
  "client": [
  ],
  "injectors": {
    "defaultRequire": 1
  }
}

'@

[System.IO.File]::WriteAllText("$root\src\main\resources\xiaoxiang_realm_expansion.mixins.json", $mixinsJson)

$gitignore = @'
# eclipse
bin
*.launch
.settings
.metadata
.classpath
.project

# idea
out
*.ipr
*.iws
*.iml
.idea

# gradle
build
.gradle

# other
eclipse
run
run-data

# The original Xiaoxiang Cultivation World mod jar and the built Xiaoxiang
# Config Extension jar, kept here only so this project can compile against
# them (compileOnly - never bundled into this mod's own built jar). Neither
# is ours to redistribute, so they must never be committed to a public repo.
libs/*.jar

# Local build output and crash dumps - not meant for a public repo.
build_log.txt
hs_err_pid*.log
replay_pid*.log

'@

[System.IO.File]::WriteAllText("$root\.gitignore", $gitignore)

$mainClass = @'
package com.xiaoxiang.realmexpansion;

import com.xiaoxiang.configext.api.IXiaoxiangExpansion;
import com.xiaoxiang.realmexpansion.config.RealmExpansionConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * Xiaoxiang Realm Expansion - adds authentic multi-layer realm progression
 * to Xiaoxiang Cultivation World (10 layers per realm: 3 Early, 3 Middle,
 * 3 Late, 1 Peak). Every layer threshold is derived from the original mod's
 * own real values - see mixin.CultivationDataLayerMixin for the formula.
 *
 * Works entirely through Mixins injecting into the original mod's compiled
 * classes at runtime. No file belonging to Xiaoxiang Cultivation World is
 * ever modified, and this mod can be uninstalled at any time with no lasting
 * effect on the base mod or on Xiaoxiang Config Extension's own settings.
 *
 * This early build implements layering for the Qi Refining realm only, as a
 * testable first pass.
 */
@Mod(XiaoxiangRealmExpansion.MOD_ID)
public class XiaoxiangRealmExpansion implements IXiaoxiangExpansion {
    public static final String MOD_ID = "xiaoxiang_realm_expansion";
    public static final String DEPENDENCY_MOD_ID = "xiaoxiang_cultivation";
    public static final Logger LOGGER = LogUtils.getLogger();

    public XiaoxiangRealmExpansion(FMLJavaModLoadingContext context) {
        if (!ModList.get().isLoaded(DEPENDENCY_MOD_ID)) {
            LOGGER.error("[{}] CRITICAL: The original mod '{}' is not installed! "
                    + "This expansion requires the Xiaoxiang Cultivation World mod to function. "
                    + "It does nothing on its own. Please install the original mod.",
                    MOD_ID, DEPENDENCY_MOD_ID);
            return;
        }

        // Register our own normal Forge config, so this mod is fully configurable
        // even without Xiaoxiang Config Extension installed.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RealmExpansionConfig.SPEC);

        LOGGER.info("[{}] Xiaoxiang Realm Expansion initialized - requires '{}'. "
                + "Layered progression is currently implemented for Qi Refining only.",
                MOD_ID, DEPENDENCY_MOD_ID);
    }

    // --- Xiaoxiang Config Extension integration ---
    //
    // IXiaoxiangExpansion's instance methods below are the documented way to
    // integrate, and are implemented for correctness/future-proofing. However,
    // Config Extension's current auto-discovery code (ExpansionDiscovery.java)
    // actually invokes getConfigSpec()/getDisplayName() as STATIC methods
    // (Method.invoke(null, ...)), so it silently fails to find an *instance*
    // implementation and falls through to its "static getXiaoxiangConfigSpec()"
    // fallback path instead. The static method below is what actually gets
    // picked up right now. Both are kept so this starts working the moment
    // that discovery bug is fixed on the Config Extension side too.

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return RealmExpansionConfig.SPEC;
    }

    @Override
    public String getDisplayName() {
        return "Xiaoxiang Realm Expansion";
    }

    /**
     * The method Config Extension's auto-discovery currently actually calls
     * (as a static invocation). See the note above.
     */
    public static ForgeConfigSpec getXiaoxiangConfigSpec() {
        return RealmExpansionConfig.SPEC;
    }
}

'@

[System.IO.File]::WriteAllText("$root\src\main\java\com\xiaoxiang\realmexpansion\XiaoxiangRealmExpansion.java", $mainClass)

$configClass = @'
package com.xiaoxiang.realmexpansion.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Config for Xiaoxiang Realm Expansion.
 *
 * If Xiaoxiang Config Extension is installed, this spec is registered with it
 * automatically (see XiaoxiangRealmExpansion.getConfigSpec()) and shows up in
 * its config screen like any other category. Without Config Extension, these
 * same values are still fully editable by hand in
 * xiaoxiang_realm_expansion-common.toml in your config folder.
 */
public class RealmExpansionConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_QI_REFINING_LAYERS;
    public static final ForgeConfigSpec.EnumValue<LayerPreset> QI_REFINING_LAYER_PRESET;
    public static final ForgeConfigSpec.DoubleValue QI_REFINING_LAYER_LIFESPAN_BONUS_YEARS;

    static {
        BUILDER.push("qiRefining");

        BUILDER.comment(
                "Test scope for this early build: Qi Refining is currently the only realm",
                "with layered progression. Turning this off restores completely vanilla",
                "Qi Refining behavior (a single Early/Middle/Late/Peak breakthrough each),",
                "exactly as if this mod were not installed."
        ).push("layers");

        ENABLE_QI_REFINING_LAYERS = BUILDER
                .comment("Split Qi Refining's Early/Middle/Late sub-stages into individually-numbered layers, plus a single Peak layer.")
                .define("enableQiRefiningLayers", true);

        QI_REFINING_LAYER_PRESET = BUILDER
                .comment(
                        "How many layers Qi Refining is split into. This is a fixed picklist, not a",
                        "free number - pick the tier you want.",
                        "",
                        "STANDARD_10 reproduces the base mod's own Early/Middle/Late/Peak Qi",
                        "requirements exactly, unscaled (Early=200, Middle=300, Late=400, sourced",
                        "directly from the base mod). Every higher preset scales those same real",
                        "numbers up using (layerCount/10)^2 - e.g. LAYERS_15 makes Early's real 200",
                        "requirement become 450 (2.25x), because going deeper should mean each layer",
                        "asks more of you, not just adding more of the same-sized steps.",
                        "",
                        "Peak is always exactly 1 layer, whichever preset you choose, and its",
                        "requirement is always derived as the sum of everything Late's layers",
                        "required to clear - i.e. clearing Peak costs as much as the entire Late",
                        "stage you just finished, combined."
                )
                .defineEnum("qiRefiningLayerPreset", LayerPreset.STANDARD_10);

        QI_REFINING_LAYER_LIFESPAN_BONUS_YEARS = BUILDER
                .comment(
                        "A small lifespan bonus (in years) granted for each INTRA-substage layer",
                        "breakthrough (e.g. layer 1->2, 2->3 - NOT the sub-stage-clearing layer,",
                        "and not Peak). This is on top of - not instead of - the normal, much larger",
                        "lifespan gain from a full realm breakthrough, which is still governed",
                        "entirely by Xiaoxiang Config Extension's own lifespan settings (or the base",
                        "mod's defaults if Config Extension isn't installed). More layers means more",
                        "of these bonuses add up over a full realm, by design. Set to 0 to disable."
                ).defineInRange("qiRefiningLayerLifespanBonusYears", 1.0, 0.0, 1000.0);

        BUILDER.pop(); // layers
        BUILDER.pop(); // qiRefining

        SPEC = BUILDER.build();
    }

    private RealmExpansionConfig() {
    }
}

'@

[System.IO.File]::WriteAllText("$root\src\main\java\com\xiaoxiang\realmexpansion\config\RealmExpansionConfig.java", $configClass)

$layerPresetEnum = @'
package com.xiaoxiang.realmexpansion.config;

/**
 * Standardized, hardcoded set of layer counts a player can choose per realm.
 * Deliberately NOT a free-form number - a fixed picklist, selectable as a
 * dropdown in the config screen.
 *
 * The standard preset (10) reproduces the base mod's own Early/Middle/Late/Peak
 * values exactly, unscaled. Every other preset scales those same real values up
 * using (layerCount / 10)^2 - see CultivationDataLayerMixin for the full formula.
 */
public enum LayerPreset {
    STANDARD_10(10),
    LAYERS_12(12),
    LAYERS_13_PERFECTION(13),
    LAYERS_15(15),
    LAYERS_20(20),
    LAYERS_25(25),
    LAYERS_33(33),
    LAYERS_50(50),
    LAYERS_100(100);

    private final int layerCount;

    LayerPreset(int layerCount) {
        this.layerCount = layerCount;
    }

    /**
     * Total layers for this preset, always including the single Peak layer
     * as the final one (e.g. 10 -> 3 Early + 3 Middle + 3 Late + 1 Peak).
     */
    public int layerCount() {
        return layerCount;
    }
}

'@

[System.IO.File]::WriteAllText("$root\src\main\java\com\xiaoxiang\realmexpansion\config\LayerPreset.java", $layerPresetEnum)

$layerMixin = @'
package com.xiaoxiang.realmexpansion.mixin;

import com.xiaoxiang.realmexpansion.config.LayerPreset;
import com.xiaoxiang.realmexpansion.config.RealmExpansionConfig;
import com.xiaoxiang.cultivation.cultivation.CultivationData;
import com.xiaoxiang.cultivation.cultivation.CultivationProgressionRules;
import com.xiaoxiang.cultivation.cultivation.Physique;
import com.xiaoxiang.cultivation.cultivation.realm.Realm;
import com.xiaoxiang.cultivation.cultivation.realm.SubStage;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Splits Qi Refining's Early/Middle/Late sub-stages into individually-numbered
 * layers, plus a single Peak layer, using a picklist of standardized layer
 * counts (see LayerPreset) - never a free-form number.
 *
 * THE MATH, using values already in the base mod (nothing invented):
 *
 * 1. Bucketing: for a chosen total layer count L, Peak is always exactly the
 *    final layer (#L), never subdivided. The remaining L-1 layers are split
 *    as evenly as possible across Early/Middle/Late using standard bucket
 *    division (realmexp$bucketOf), so no group is ever starved and remainders
 *    land predictably.
 *
 * 2. Scaling: CultivationProgressionRules.maxCultivation(realm, subStage,
 *    physique) is literally `Realm.maxQi(subStage) * physiqueMultiplier` -
 *    the exact real number the base mod already uses (and which already
 *    reflects any Xiaoxiang Config Extension override to that value, since
 *    this reads Config Extension's own already-modified result). At the
 *    standard 10-layer preset that real number is used completely unscaled.
 *    Every higher preset multiplies it by (L/10)^2 - double the layers means
 *    quadruple the requirement, so going deeper asks progressively more of
 *    you, not just adding more same-sized steps.
 *
 * 3. Per-layer requirement within a bucket of size k, at scaled anchor V:
 *    layer j (1..k) requires round(V * j/k). The LAST layer of a bucket
 *    (j==k) always lands exactly on the scaled anchor - i.e. the real
 *    Early->Middle->Late breakthrough gates, just bigger than vanilla by
 *    whatever the chosen preset's multiplier is (1x at the standard preset).
 *
 * 4. Peak is handled entirely differently: its requirement is the SUM of
 *    everything Late's own layers required to clear, i.e. clearing Peak
 *    costs as much as the whole Late stage you just finished, combined.
 *    Peak does not use its own separate base-mod anchor at all.
 *
 * ADVANCEMENT: advanceOnSuccess() is intercepted so that finishing an
 * intra-bucket layer (not the bucket's last layer) does NOT trigger the base
 * mod's sub-stage change - it resets progress, grants a small configurable
 * lifespan bonus, and moves to the next layer. Finishing a bucket's last
 * layer, or Peak, lets the base mod's own advancement logic run exactly as
 * it always has.
 *
 * The current layer (1..L) is tracked in a brand-new field added to
 * CultivationData at runtime via @Unique - this does not alter any file
 * belonging to Xiaoxiang Cultivation World, and is persisted through the
 * base mod's own existing serializeNBT()/deserializeNBT() methods. A
 * character already partway through Qi Refining before this mod is
 * installed (or after a layer preset change) self-heals to the last layer
 * of whichever bucket matches their real sub-stage the first time it's
 * read, so layering only ever engages starting from their NEXT breakthrough.
 *
 * Everything here is scoped to realm == QI_REFINING only, and is a total
 * no-op the instant it's disabled in config, for every other realm - this is
 * a first testable pass, not yet extended to the rest of the realm ladder.
 */
@Mixin(CultivationData.class)
public abstract class CultivationDataLayerMixin {

    @Unique
    private int realmexp$qiRefiningLayer = 0; // 0 = not yet initialized

    // ------------------------------------------------------------------
    // Bucketing helpers (bucket 0 = Early, 1 = Middle, 2 = Late; Peak is
    // never a bucket member - it's always exactly layer L).
    // ------------------------------------------------------------------

    @Unique
    private static int realmexp$bucketOf(int p, int totalLayers) {
        // p = position among the L-1 non-Peak layers, 1-indexed
        return ((p - 1) * 3) / (totalLayers - 1);
    }

    @Unique
    private static int realmexp$bucketSize(int bucketIndex, int totalLayers) {
        int count = 0;
        for (int p = 1; p <= totalLayers - 1; p++) {
            if (realmexp$bucketOf(p, totalLayers) == bucketIndex) count++;
        }
        return count;
    }

    @Unique
    private static int realmexp$positionInBucket(int p, int totalLayers) {
        int bucket = realmexp$bucketOf(p, totalLayers);
        int pos = 0;
        for (int q = 1; q <= p; q++) {
            if (realmexp$bucketOf(q, totalLayers) == bucket) pos++;
        }
        return pos;
    }

    @Unique
    private static int realmexp$lastLayerOfBucket(int bucketIndex, int totalLayers) {
        int last = 0;
        for (int p = 1; p <= totalLayers - 1; p++) {
            if (realmexp$bucketOf(p, totalLayers) <= bucketIndex) last = p;
        }
        return last;
    }

    /**
     * Lazily resolves/self-heals the current layer (1..L) from real sub-stage
     * state the first time it's needed, so existing characters (or ones with
     * no saved layer yet, or who just had their layer preset changed) land on
     * the layer that reproduces vanilla-equivalent behavior for whatever
     * sub-stage they're already in.
     */
    @Unique
    private int realmexp$layer(CultivationData self, int totalLayers) {
        if (this.realmexp$qiRefiningLayer <= 0 || this.realmexp$qiRefiningLayer > totalLayers) {
            SubStage sub = self.getSubStage();
            if (sub == SubStage.PEAK) {
                this.realmexp$qiRefiningLayer = totalLayers;
            } else if (sub == SubStage.LATE) {
                this.realmexp$qiRefiningLayer = realmexp$lastLayerOfBucket(2, totalLayers);
            } else if (sub == SubStage.MIDDLE) {
                this.realmexp$qiRefiningLayer = realmexp$lastLayerOfBucket(1, totalLayers);
            } else {
                this.realmexp$qiRefiningLayer = realmexp$lastLayerOfBucket(0, totalLayers);
            }
        }
        return this.realmexp$qiRefiningLayer;
    }

    @Unique
    private static double realmexp$multiplier(int totalLayers) {
        double ratio = totalLayers / 10.0;
        return ratio * ratio;
    }

    // ------------------------------------------------------------------
    // Qi requirement
    // ------------------------------------------------------------------

    @Inject(method = "getMaxCultivation()J", at = @At("RETURN"), cancellable = true, remap = false)
    private void realmexp$scaleMaxCultivation(CallbackInfoReturnable<Long> cir) {
        if (!RealmExpansionConfig.ENABLE_QI_REFINING_LAYERS.get()) return;
        CultivationData self = (CultivationData) (Object) this;
        if (self.getRealm() != Realm.QI_REFINING) return;

        LayerPreset preset = RealmExpansionConfig.QI_REFINING_LAYER_PRESET.get();
        int totalLayers = preset.layerCount();
        double multiplier = realmexp$multiplier(totalLayers);
        int layer = realmexp$layer(self, totalLayers);

        if (self.getSubStage() == SubStage.PEAK) {
            // Peak's requirement is derived from Late's total - the sum of
            // everything Late's own layers required to clear - not from the
            // base mod's separate Peak anchor at all.
            Physique physique = self.getPhysique();
            long lateFull = CultivationProgressionRules.maxCultivation(Realm.QI_REFINING, SubStage.LATE, physique);
            long scaledLateAnchor = Math.round(lateFull * multiplier);
            int lateBucketSize = realmexp$bucketSize(2, totalLayers);
            long peakReq = 0;
            for (int j = 1; j <= lateBucketSize; j++) {
                peakReq += Math.round(scaledLateAnchor * (j / (double) lateBucketSize));
            }
            cir.setReturnValue(Math.max(1L, peakReq));
            return;
        }

        int bucketIdx = realmexp$bucketOf(layer, totalLayers);
        int k = realmexp$bucketSize(bucketIdx, totalLayers);
        int j = realmexp$positionInBucket(layer, totalLayers);

        long fullOriginal = cir.getReturnValue(); // already reflects any Config Extension override
        long scaledAnchor = Math.round(fullOriginal * multiplier);
        long req = Math.round(scaledAnchor * (j / (double) k));
        cir.setReturnValue(Math.max(1L, req));
    }

    // ------------------------------------------------------------------
    // Breakthrough advancement
    // ------------------------------------------------------------------

    @Inject(method = "advanceOnSuccess()V", at = @At("HEAD"), cancellable = true, remap = false)
    private void realmexp$advanceOnSuccess(CallbackInfo ci) {
        if (!RealmExpansionConfig.ENABLE_QI_REFINING_LAYERS.get()) return;
        CultivationData self = (CultivationData) (Object) this;
        if (self.getRealm() != Realm.QI_REFINING) return;
        if (self.getSubStage() == SubStage.PEAK) return; // breaking out of Qi Refining entirely - untouched

        LayerPreset preset = RealmExpansionConfig.QI_REFINING_LAYER_PRESET.get();
        int totalLayers = preset.layerCount();
        int layer = realmexp$layer(self, totalLayers);

        int bucketIdx = realmexp$bucketOf(layer, totalLayers);
        int k = realmexp$bucketSize(bucketIdx, totalLayers);
        int j = realmexp$positionInBucket(layer, totalLayers);

        if (j >= k) {
            // The real sub-stage breakthrough. Advance our layer counter to
            // the first layer of the new bucket (or to Peak), then let the
            // base mod's own logic run normally to do the actual change.
            this.realmexp$qiRefiningLayer = layer + 1;
            return;
        }

        // Intra-bucket layer breakthrough (e.g. layer 1 -> 2). The base
        // mod's advanceOnSuccess() would incorrectly jump straight to the
        // next sub-stage here, so we cancel it and handle this step
        // ourselves.
        this.realmexp$qiRefiningLayer = layer + 1;
        self.setCultivationProgress(0L);

        double bonusYears = RealmExpansionConfig.QI_REFINING_LAYER_LIFESPAN_BONUS_YEARS.get();
        if (bonusYears > 0) {
            // boneAge counts UP toward the realm's lifespan cap, so a small
            // negative delta here is a small lifespan bonus - the same
            // mechanism the base mod's own rejuvenation pills use.
            self.addBoneAge(-bonusYears);
        }

        ci.cancel();
    }

    // ------------------------------------------------------------------
    // Persistence
    // ------------------------------------------------------------------

    @Inject(method = "serializeNBT()Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"), remap = false)
    private void realmexp$writeLayer(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        if (tag != null) {
            tag.putInt("realmexp_qiRefiningLayer", this.realmexp$qiRefiningLayer);
        }
    }

    @Inject(method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"), remap = false)
    private void realmexp$readLayer(CompoundTag tag, CallbackInfo ci) {
        this.realmexp$qiRefiningLayer = (tag != null && tag.contains("realmexp_qiRefiningLayer"))
                ? tag.getInt("realmexp_qiRefiningLayer")
                : 0; // 0 triggers the self-healing lazy resolve on next read
    }
}

'@

[System.IO.File]::WriteAllText("$root\src\main\java\com\xiaoxiang\realmexpansion\mixin\CultivationDataLayerMixin.java", $layerMixin)

Write-Host ""
Write-Host "Done. Project created at $root" -ForegroundColor Green
Write-Host ""
Write-Host "To test it:" -ForegroundColor Green
Write-Host "  1. Open a terminal in that folder"
Write-Host "  2. Run: gradlew.bat runClient"
Write-Host "  (first run downloads a lot of Forge/MDK tooling and takes a while - that is normal)"
Write-Host ""
Write-Host "In-game test: start a Qi Refining cultivator, break through repeatedly, and watch it"
Write-Host "take exactly 3 breakthroughs to go from Early to Middle (each one visibly faster than"
Write-Host "a full vanilla breakthrough), instead of the original single jump."
