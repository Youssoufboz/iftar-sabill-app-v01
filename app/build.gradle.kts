plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ramadan.sabil23"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ramadan.sabil23"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

        // Existing dependencies...

        // Gson dependency
        implementation (libs.gson)
    implementation(libs.play.services.maps.v1820)
    implementation(libs.play.services.location.v2101)
    implementation(libs.places)

    // Retrofit for network requests
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Glide for image loading
    implementation(libs.glide)

    // Maps Utils for marker clustering
    implementation(libs.android.maps.utils)

    // Material Components
    implementation(libs.material.v1100)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.espresso.core.v351)
}