package es.goda87.build

import com.android.build.gradle.LibraryExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// from https://www.zacsweers.dev/kapts-hidden-test-costs/
fun Project.disableKaptFromTests() {
    tasks
        .matching {
            it.name.startsWith("kapt") &&
                it.name.endsWith("TestKotlin")
        }
        .configureEach { enabled = false }
}

fun Project.setupKotlin(
    warningsAsErrors: Boolean = project.hasProperty("warningsAsErrors"),
) {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            allWarningsAsErrors = warningsAsErrors
            jvmTarget = libs().getVersion("java")
        }
    }
}

fun Project.setupSpotless() {
    pluginManager.apply("com.diffplug.spotless")

    extensions.configure<SpotlessExtension> {
        kotlin {
            pluginManager.withAndroidPlugin {
                target("src/*/java/**/*.kt", "src/*/kotlin/**/*.kt")
            }
            ktlint(libs().getVersion("ktlint"))
                .editorConfigOverride(
                    mapOf(
                        "insert_final_newline" to true,
                        "end_of_line" to "lf",
                        "ij_kotlin_allow_trailing_comma" to true,
                        "ij_kotlin_allow_trailing_comma_on_call_site" to true,
                        "disabled_rules" to "filename",
                    )
                )
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint(libs().getVersion("ktlint"))
                .editorConfigOverride(
                    mapOf(
                        "insert_final_newline" to true,
                        "end_of_line" to "lf",
                        "ij_kotlin_allow_trailing_comma" to true,
                        "ij_kotlin_allow_trailing_comma_on_call_site" to true,
                        "disabled_rules" to "filename",
                    )
                )
        }
    }
}

fun Project.setupDetekt(vararg buildVariats: String) {
    pluginManager.apply("io.gitlab.arturbosch.detekt")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        baseline = file("baseline.xml")
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = libs().getVersion("java")
    }

    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = libs().getVersion("java")
    }

    dependencies {
        "detektPlugins"("com.braisgabin.detekt:kotlin-compiler-wrapper:0.0.2")
        "detektPlugins"("com.braisgabin.detekt:junit:0.0.4")
        "detektPlugins"("ru.kode:detekt-rules-compose:1.1.0")
    }

    tasks.register("detektAll") {
        group = "verification"
        description = "Run detekt analysis in all the modules."

        dependsOn(
            if (buildVariats.isEmpty()) {
                listOf(
                    project.tasks.named("detektMain"),
                    project.tasks.named("detektTest"),
                )
            } else {
                buildVariats.flatMap { variant ->
                    listOf(
                        project.tasks.named("detekt$variant"),
                        project.tasks.named("detekt${variant}UnitTest"),
                    )
                }
            },
        )
    }

    tasks.register("detektBaselineAll") {
        group = "verification"
        description = "Creates a detekt baseline in all the modules."

        dependsOn(
            if (buildVariats.isEmpty()) {
                listOf(
                    project.tasks.named("detektBaselineMain"),
                    project.tasks.named("detektBaselineTest"),
                )
            } else {
                buildVariats.flatMap { variant ->
                    listOf(
                        project.tasks.named("detektBaseline$variant"),
                        project.tasks.named("detektBaseline${variant}UnitTest"),
                    )
                }
            },
        )
    }
}

fun Project.setupViewBinding() {
    extensions.configure<LibraryExtension>("android") {
        buildFeatures {
            viewBinding = true
        }
    }
}

fun Project.setupCompose() {
    extensions.configure<LibraryExtension>("android") {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs().getVersion("compose-compiler")
        }
    }
}

internal fun Project.libs(): VersionCatalog {
    return extensions.getByType<VersionCatalogsExtension>().named("libs")
}

internal val Project.sdkTarget: Int get() = libs().getVersion("sdk-target").toInt()
internal val Project.sdkCompile: Int get() = libs().getVersion("sdk-compile").toInt()
internal val Project.sdkMin: Int get() = libs().getVersion("sdk-min").toInt()
internal val Project.javaVersion: String get() = libs().getVersion("java")


internal fun VersionCatalog.getVersion(feature: String): String {
    return findVersion(feature).get().toString()
}

internal fun Project.calculateNamespace(): String? {
    return if (isFeature() || isIntegration() || isCommon() || isLib()) {
        val packagePostFix = name.split("-")
            .map { if (it == "public") "publicapi" else it }
            .joinToString(".")
        "es.goda87.$packagePostFix"
    } else {
        null
    }
}

private fun PluginManager.withAndroidPlugin(block: AppliedPlugin.() -> Unit) {
    withPlugin("com.android.library", block)
    withPlugin("com.android.application", block)
}

private fun Project.isFeature() = name.startsWith("features-")

private fun Project.isIntegration() = name.startsWith("integrations-")

private fun Project.isCommon() = name.startsWith("commons-")

private fun Project.isLib() = name.startsWith("libs-")
