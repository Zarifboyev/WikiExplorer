plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.z99a.wikipedia"

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.z99a.wikipedia"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.work)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi.kotlin)

    // Google Play Services & Maps
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.places)

    // UI Utilities
    implementation(libs.picasso)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.browser)
    implementation(libs.viewbinding.delegate)
    implementation(libs.timber)



    // Missed ones
    implementation(libs.androidx.core.ktx)
    // lottie
    // For standard XML-based projects
    implementation(libs.lottie)

    // OR for Jetpack Compose projects
    // USE when its jetpack compose
//    implementation(libs.lottie.compose)
    implementation(libs.androidx.work.runtime.ktx)

}