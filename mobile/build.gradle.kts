import com.android.build.api.dsl.Lint
import com.android.build.api.dsl.LintOptions
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.utils.property
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mikepenz.aboutlibrary)
    alias(libs.plugins.google.firebase.crashlytics)


}

android {

    fun Lint.() {
        checkReleaseBuilds = false
    }



    namespace = "dev.charan.batteryTracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.charan.batteryTracker"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val apiKey = properties.getProperty("API_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "API_KEY",
            value = apiKey
        )
    }
    buildFeatures {
        buildConfig = true
        compose =true
    }
    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                load(project.rootProject.file("local.properties").inputStream())
            }
            keyAlias = properties.getProperty("KEY_ALIAS") ?: ""
            keyPassword = properties.getProperty("KEY_PASSWORD") ?: ""
            storeFile = file(properties.getProperty("KEY_LOCATION") ?: "")
            storePassword = properties.getProperty("KEY_STORE_PASSWORD") ?: ""
        }
    }



    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true


        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    hilt { enableAggregatingTask = false }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

   implementation(libs.material)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.glance)
    implementation (libs.accompanist.swiperefresh)
    implementation(libs.androidx.material3.android)

    // For AppWidgets support
    implementation (libs.androidx.glance.appwidget)
    debugImplementation(libs.leakcanary.android)



    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)
    implementation (libs.play.services.wearable)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.version.tracker.android.library)
    implementation (libs.hilt.android)
    kapt (libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)
    implementation (libs.accompanist.permissions)
    implementation (libs.androidx.hilt.work)
    implementation(libs.splash.screen)
    implementation(libs.kotlinx.serialization.json)
    implementation (libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.m3)




    wearApp(project(":wear"))
    implementation(libs.androidx.work.runtime)
}