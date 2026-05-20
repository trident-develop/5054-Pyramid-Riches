import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.lsparanoid)
}

lsparanoid {
    seed = 38
    classFilter = { it.startsWith("com.dragonest.artifacts.goo") }
    includeDependencies = true
    variantFilter = { true }
}

android {
    namespace = "com.dragonest.artifacts.goo"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.dragonest.artifacts.goo"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.orbit.viewmodel)
    implementation(libs.orbit.compose)
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.okhttp)
    implementation(libs.installreferrer)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.ads)
    implementation(libs.firebase.messaging)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

afterEvaluate {
    tasks.named("uploadCrashlyticsMappingFileRelease")
        .configure { enabled = false }
}

afterEvaluate {
    tasks.named("bundleRelease").configure {
        finalizedBy("removeProguardMap")
    }
}

tasks.register("removeProguardMap") {
    doLast {
        val generatedAabPath = "${projectDir}/release"
        val aabFile = file("${generatedAabPath}/app-release.aab")


        val zipFile = file("${generatedAabPath}/app-release.zip")
        val savedProguardMapFile = file("${generatedAabPath}/proguard.map")
        val tempZipFilePath = file("${generatedAabPath}/app-release-temp.zip")
        val targetFilePath = "BUNDLE-METADATA/com.android.tools.build.obfuscation/proguard.map"


        aabFile.renameTo(zipFile)


        val zf = ZipFile(zipFile)
        val zos = ZipOutputStream(tempZipFilePath.outputStream())
        try {
            val entries = zf.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                if (entry.name != targetFilePath) {
                    zos.putNextEntry(ZipEntry(entry.name))
                    zf.getInputStream(entry).use { it.copyTo(zos) }
                    zos.closeEntry()
                } else {
                    zf.getInputStream(entry).use { input ->
                        savedProguardMapFile.outputStream().use { input.copyTo(it) }
                    }
                }
            }
        } finally {
            zos.close()
            zf.close()
        }


        zipFile.delete()
        tempZipFilePath.renameTo(aabFile)
    }
}