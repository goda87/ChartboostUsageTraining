package es.goda87.build

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.builder.core.BuilderConstants.DEBUG
import com.android.builder.core.BuilderConstants.RELEASE
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class AndroidApplication : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply()
    }
}

private fun Project.apply() {
    pluginManager.apply("com.android.application")
    pluginManager.apply("kotlin-android")
    pluginManager.apply("kotlin-kapt")

    extensions.configure<BaseAppModuleExtension>("android") {
        compileSdk = sdkCompile

        defaultConfig {
            minSdk = sdkMin
            targetSdk = sdkTarget
        }

        namespace = calculateNamespace()

        buildFeatures {
            buildConfig = true
            resValues = true
        }

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
                matchingFallbacks += listOf(RELEASE)
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
}
