package dev.charan.batteryTracker.presentation.settings

import android.util.Rational

sealed interface SettingsEffect {
    data object RequestNotificationPermission : SettingsEffect
    data object RequestNertByPermission : SettingsEffect
    data object OpenSettings : SettingsEffect
    data class OpenGithub(val url: String ) : SettingsEffect
}