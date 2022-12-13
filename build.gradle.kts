buildscript {
    val compose_ui_version by extra("1.1.1")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version libs.versions.android.tools.get() apply false
    id("com.android.library") version libs.versions.android.tools.get() apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
}

tasks.register("clean",Delete::class) {
    delete(rootProject.buildDir)
}
