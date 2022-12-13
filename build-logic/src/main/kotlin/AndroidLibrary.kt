package es.goda87.build

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.android.builder.core.BuilderConstants.DEBUG
import com.android.builder.core.BuilderConstants.RELEASE
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibrary : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply()
    }
}

private fun Project.apply() {
    pluginManager.apply("com.android.library")
    pluginManager.apply("kotlin-android")
    pluginManager.apply("kotlin-kapt")

    extensions.configure<LibraryExtension>("android") {
        compileSdk = sdkCompile

        defaultConfig {
            minSdk = sdkMin
            targetSdk = sdkTarget
        }

        namespace = calculateNamespace()

        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(javaVersion)
            targetCompatibility = JavaVersion.toVersion(javaVersion)
        }

        testOptions {
            unitTests.all {
                it.useJUnitPlatform {
                    includeEngines("junit-jupiter")
                }
            }
        }

        buildTypes {
            getByName(DEBUG) {
                matchingFallbacks += RELEASE
            }
        }
    }

    extensions.configure<LibraryAndroidComponentsExtension>("androidComponents") {
        beforeVariants {
            if (it.buildType == "debug") {
                it.enable = false
            }
        }
    }

    setupKotlin()
    setupSpotless()
    setupDetekt()
    disableKaptFromTests()

    dependencies {
        "implementation"(platform(project(":bom")))
        pluginManager.withPlugin("kotlin-kapt") {
            "kapt"(platform(project(":bom")))
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
}
