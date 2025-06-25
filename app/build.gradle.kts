plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.behnamuix.metremajazi"
    compileSdk = 35
    buildFeatures{
        viewBinding=true //این کد
    }


    defaultConfig {
        applicationId = "com.behnamuix.metremajazi"
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.constraintlayout.v214)

    // وابستگی‌های ARCore
    implementation(libs.core.v1400)
    implementation(libs.sceneform.core)
    implementation("com.google.ar.sceneform.ux:sceneform-ux:1.17.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation(libs.androidx.activity)
    implementation("com.google.ar.sceneform:assets:1.17.1")


    implementation("ir.tapsell.plus:tapsell-plus-sdk-android:2.3.2")




}