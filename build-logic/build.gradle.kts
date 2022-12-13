plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:${libs.versions.android.tools.get()}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${libs.versions.spotless.get()}")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${libs.versions.detekt.get()}")
}

gradlePlugin {
    plugins {
        create("KmpModule") {
            id = "common-kmp-feature"
            implementationClass = "es.goda87.build.KmpModule"
        }
        create("JvmModule") {
            id = "common-jvm-feature"
            implementationClass = "es.goda87.build.JvmModule"
        }
        create("AndroidLibrary") {
            id = "common-android-feature"
            implementationClass = "es.goda87.build.AndroidLibrary"
        }
        create("AndroidApplication") {
            id = "common-android-application"
            implementationClass = "es.goda87.build.AndroidApplication"
        }
    }
}
