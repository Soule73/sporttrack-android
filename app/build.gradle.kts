plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10" // Pour la sérialisation JSON
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.stapp.sporttrack"
    compileSdk = 35

    defaultConfig {

        applicationId = "com.stapp.sporttrack"
        minSdk = 26
        // minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
        }

        release {
            isMinifyEnabled = false //  true : pour activer la minification en release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

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

    implementation(libs.mpandroidchart)

    // Dependency to include Maps SDK for Android
//    implementation(libs.play.services.maps)

    // Google Maps Compose library
    implementation(libs.maps.compose)
    // Google Maps Compose utility library
    implementation(libs.maps.compose.utils)
    // Google Maps Compose widgets library
    implementation(libs.maps.compose.widgets)
    implementation(libs.play.services.location)

    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.foundation.layout.android)


    runtimeOnly(libs.accompanist.pager)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}