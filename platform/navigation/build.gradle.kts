plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.funapp.android.platform.navigation"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":model"))
    implementation(project(":platform:ui-components"))
    implementation(project(":services:network"))
    implementation(project(":services:favorites"))
    implementation(project(":services:search"))
    implementation(project(":services:ai"))
    implementation(project(":features:login"))
    implementation(project(":features:home"))
    implementation(project(":features:search"))
    implementation(project(":features:items"))
    implementation(project(":features:profile"))
    implementation(project(":features:profile-detail"))
    implementation(project(":features:detail"))
    implementation(project(":features:settings"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
}
