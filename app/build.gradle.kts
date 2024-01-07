plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.battery_widget"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.battery_widget"
        minSdk = 31
        targetSdk = 33
        versionCode = 3
        versionName = "3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.2.0-alpha12")
    implementation("androidx.compose.material3:material3-window-size-class-android:1.2.0-alpha10")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.glance:glance:1.0.0")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.27.0")

    // For AppWidgets support
    implementation ("androidx.glance:glance-appwidget:1.0.0")
    implementation ("androidx.glance:glance-material3:1.0.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation ("androidx.compose.material:material-icons-extended:1.6.0-beta03")
    implementation ("androidx.navigation:navigation-compose:2.7.6")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
    implementation ("androidx.datastore:datastore:1.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.33.1-alpha")



}