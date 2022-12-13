package es.goda87.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType

class JvmModule : Plugin<Project> {

    override fun apply(target: Project) {
        target.apply()
    }
}

private fun Project.apply() {
    pluginManager.apply("kotlin")
    pluginManager.apply("kotlin-kapt")

    extensions.configure<JavaPluginExtension>("java") {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    setupKotlin()
    setupSpotless()
    setupDetekt()
    disableKaptFromTests()
    setupJunit()

    dependencies {
        "implementation"(platform(project(":bom")))
        pluginManager.withPlugin("kotlin-kapt") {
            "kapt"(platform(project(":bom")))
        }
    }
}

private fun Project.setupJunit() {
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
