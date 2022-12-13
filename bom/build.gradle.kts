plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {

    api(platform("com.squareup.okhttp3:okhttp-bom:${libs.versions.okhttp.get()}"))
    api(platform("org.jetbrains.kotlin:kotlin-bom:${libs.versions.kotlin.get()}"))
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${libs.versions.coroutines.get()}"))
    api(platform("org.junit:junit-bom:${libs.versions.junit.get()}"))
    api(platform("org.mockito:mockito-bom:${libs.versions.mockito.get()}"))
}
