plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val appVersionName = "1.0"

// Имя APK: vasulyatr-1.0-debug.apk / vasulyatr-1.0-release.apk вместо app-*.apk.
base {
    archivesName = "vasulyatr-$appVersionName"
}

android {
    namespace = "io.github.iliasa101.vasulyatr"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.iliasa101.vasulyatr"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = appVersionName
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            // Подпись релиза настраивается локально и в репозиторий не попадает (см. README).
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    lint {
        warningsAsErrors = false
        abortOnError = true
        checkDependencies = true
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
}
