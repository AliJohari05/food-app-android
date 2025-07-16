// FoodApp/app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "com.alijt.foodapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alijt.foodapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Exclude filament-android from material library if it's pulling it transitively
    implementation("com.google.android.material:material:1.10.0") {
        exclude(group = "com.google.ar.sceneform")
        // or a more specific exclusion if the group contains other useful libraries:
        // exclude(group = "com.google.ar.sceneform", module = "filament-android")
    }

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp logging interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // تأیید کنید که این خط وجود دارد

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.inappmessaging)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // یا ksp برای آخرین AGP

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // بروزرسانی به آخرین نسخه
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2") // بروزرسانی به آخرین نسخه

    // SharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

    // Android KTX, Compose, Material3 (ensure these are compatible with compileSdk 36)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}