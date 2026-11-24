plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.resepku.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.resepku.app"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
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
    // ===== Compose BOM (menentukan versi semua library Compose) =====
    implementation(platform(libs.androidx.compose.bom))

    // ===== Core Android =====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ===== Compose UI =====
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // ===== Navigation Compose =====
    implementation(libs.androidx.navigation.compose)

    // ===== ViewModel + Compose =====
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ===== Retrofit (HTTP client untuk REST API) =====
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)

    // ===== OkHttp + Logging (debug HTTP calls) =====
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)

    // ===== Gson (JSON serialization) =====
    implementation(libs.gson)

    // ===== DataStore Preferences (simpan token) =====
    implementation(libs.androidx.datastore.preferences)

    // ===== Coil (load gambar dari URL) =====
    implementation(libs.coil.compose)

    // ===== Testing =====
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
