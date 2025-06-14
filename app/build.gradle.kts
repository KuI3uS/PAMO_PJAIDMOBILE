plugins {
    alias(libs.plugins.android.application)
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.pjaidmobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pjaidmobile"
        minSdk = 27
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.zxing.android.embedded)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
// RxJava3
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
// Retrofit RxJava Adapter
    implementation (libs.retrofit2.adapter.rxjava3)
// Lifecycle (ViewModel, LiveData)
    implementation (libs.lifecycle.viewmodel)
    implementation (libs.androidx.lifecycle.livedata)
    implementation (libs.lifecycle.common.java8)
// Google Maps
    implementation(libs.play.services.maps)

// Localization
    implementation(libs.play.services.location)

// Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.uiautomator)
    annotationProcessor(libs.dagger.hilt.compiler)
    testImplementation(libs.espresso.core)
    testImplementation(libs.ext.junit)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
// Run espresso tests
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
}