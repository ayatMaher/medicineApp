plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-android")
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.medicineapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.medicineapplication"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.circleimageview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.google.material)
    implementation(libs.androidx.annotation)
    implementation(libs.play.services.location)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //noinspection UseTomlInstead
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    //noinspection UseTomlInstead
    implementation(libs.glide)
    implementation(libs.play.services.location.v2101)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation (libs.okhttp)
    // Glide
    //noinspection KaptUsageInsteadOfKsp
   // kapt(libs.compiler)
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth.v2100)
    implementation(libs.facebook.android.sdk)
  
    // barcode

    implementation (libs.barcode.scanning)
    // Glide
    implementation(libs.glide) // هذا جيد
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    // بدل libs.compiler
    // ZXing QR Scanner
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.5.0")

    implementation(libs.zxing.core)
    implementation(libs.zxing.embedded)

    implementation("com.google.firebase:firebase-messaging")




    implementation(libs.barcode.scanning)

    // اختبارات الوحدة (Unit Tests)
    testImplementation(libs.junit)

// لدعم الكوروتينز في الاختبارات (إذا تستخدمها)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    implementation(libs.text.recognition.v1600)








}

