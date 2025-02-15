import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

val secretsPropertiesFile = rootProject.file("keystore.properties")
val secretsProperties = Properties()
if (secretsPropertiesFile.exists()) {
    secretsProperties.load(FileInputStream(secretsPropertiesFile))
} else {
    throw FileNotFoundException("Le fichier secrets.properties est manquant.")
}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10" // Pour la sérialisation JSON
}

android {
    namespace = "com.stapp.sporttrack"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.stapp.sporttrack"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiBaseUrlDebug = secretsProperties["API_BASE_URL_DEBUG"] as String
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrlDebug\"")
    }

    buildTypes {

        debug {

            val apiBaseUrlDebug = secretsProperties["API_BASE_URL_DEBUG"] as String
            buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrlDebug\"")
        }

        release {
            isMinifyEnabled = false //  true : pour activer la minification en release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val apiBaseUrlRelease = secretsProperties["API_BASE_URL_RELEASE"] as String
            buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrlRelease\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true

        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    // OkHttp pour les requêtes réseau
    implementation(libs.okhttp)

    // Coroutines pour la programmation asynchrone
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // JSON (Kotlin Serialization)
    implementation(libs.kotlinx.serialization.json)

    // Retrofit pour simplifier les appels réseau (optionnel mais recommandé)
    implementation(libs.retrofit)
    // Intégration avec Kotlin Serialization
    implementation(libs.retrofit2.kotlinx.serialization.converter)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.filament.android)


    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // SharedPreferences sécurisées (optionnel)
    implementation(libs.androidx.security.crypto)

    //Health connect
    implementation(libs.androidx.connect.client)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}