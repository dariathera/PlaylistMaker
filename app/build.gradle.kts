plugins {
    alias(libs.plugins.android.application)
    // alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)   // вместо id("kotlin-parcelize")
    alias(libs.plugins.ksp)                 // вместо id("com.google.devtools.ksp")
}


android {
    namespace = "com.practicum.playlistmaker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.practicum.playlistmaker"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    buildFeatures {
        viewBinding = true
        compose = true
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Подключение Glide
    implementation(libs.material.v1130)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // Для работы с сетью
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)

    // Для MVVM
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)

    // внедрение зависимостей
    implementation(libs.koin.android)

    // фрагменты и навигация
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)

    // корутины
    implementation(libs.kotlinx.coroutines.android)

    // базы данных
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.ads.mobile.sdk)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


}