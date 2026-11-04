import java.io.StringReader
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val appVersionName = "1.1"

// Имя APK: vasulyator-1.1-debug.apk / vasulyator-1.1-release.apk вместо app-*.apk.
base {
    archivesName = "vasulyator-$appVersionName"
}

/**
 * Ключ подписи релиза. Источники по приоритету:
 *  1. `keystore.properties` в корне проекта (локально, в .gitignore);
 *  2. переменные окружения `VASULYATOR_*` (CI, значения из GitHub Secrets).
 * Сам ключ и пароли в репозиторий не попадают.
 */
data class ReleaseSigning(val storeFile: String, val storePassword: String, val keyAlias: String, val keyPassword: String)

val releaseSigning: ReleaseSigning? = run {
    val props = Properties()
    providers.fileContents(rootProject.layout.projectDirectory.file("keystore.properties")).asText.orNull
        ?.let { props.load(StringReader(it)) }
    fun value(key: String, env: String): String? =
        props.getProperty(key)?.takeIf { it.isNotBlank() } ?: providers.environmentVariable(env).orNull?.takeIf { it.isNotBlank() }
    ReleaseSigning(
        storeFile = value("storeFile", "VASULYATOR_KEYSTORE_FILE") ?: return@run null,
        storePassword = value("storePassword", "VASULYATOR_KEYSTORE_PASSWORD") ?: return@run null,
        keyAlias = value("keyAlias", "VASULYATOR_KEY_ALIAS") ?: return@run null,
        keyPassword = value("keyPassword", "VASULYATOR_KEY_PASSWORD") ?: return@run null,
    )
}

// Только для проверочных сборок без секретов (PR от Dependabot): R8 и lint проверяются, APK не подписан.
// Имя без точки: PowerShell режет аргумент `-Pa.b=c` по точке.
val allowUnsignedRelease = providers.gradleProperty("allowUnsignedRelease").map(String::toBoolean).getOrElse(false)

android {
    namespace = "io.github.iliasa101.vasulyator"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.iliasa101.vasulyator"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = appVersionName
    }

    signingConfigs {
        if (releaseSigning != null) {
            create("release") {
                storeFile = file(releaseSigning.storeFile)
                storePassword = releaseSigning.storePassword
                keyAlias = releaseSigning.keyAlias
                keyPassword = releaseSigning.keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
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

// Без ключа релиз не собирается молча неподписанным — падаем сразу и объясняем, что делать.
if (releaseSigning == null && !allowUnsignedRelease) {
    tasks.matching { it.name == "preReleaseBuild" }.configureEach {
        val message = """
            |Release signing key not found.
            |Put keystore.properties (storeFile, storePassword, keyAlias, keyPassword) in the project root
            |or set VASULYATOR_KEYSTORE_FILE / _KEYSTORE_PASSWORD / _KEY_ALIAS / _KEY_PASSWORD.
            |For a verification-only unsigned build pass -PallowUnsignedRelease=true.
        """.trimMargin()
        doFirst { throw GradleException(message) }
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
