buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("org.gradle.android.cache-fix") version "3.0.1" apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.mikepenz.aboutlibrary) apply false

}