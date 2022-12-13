package es.goda87.build

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

class KmpModule : Plugin<Project> {

    override fun apply(target: Project) {
        target.apply()
    }
}

private fun Project.apply() {
    pluginManager.apply("kotlin-multiplatform")
    pluginManager.apply("com.android.library")

    extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        val xcf = XCFramework(rootProject.name)
        android {
            publishLibraryVariants("release")
        }
        ios {
            binaries {
                framework {
                    baseName = rootProject.name
                    xcf.add(this)
                }
            }
        }
        iosSimulatorArm64 {
            binaries {
                framework {
                    baseName = rootProject.name
                    xcf.add(this)
                }
            }
        }

        explicitApi()
    }

    extensions.configure<LibraryExtension>("android") {
        compileSdk = sdkCompile

        defaultConfig {
            minSdk = sdkMin
            targetSdk = sdkTarget
        }

        sourceSets.getByName("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

    setupSpotless()
}
