package dev.charan.batteryTracker.data.model

import java.io.Serializable

data class AutoUpdateDTO (
   var appName: String? = null,
   var appVersion : Double?    = null,
   var appVersionCode  : Double?    = null,
   var appDownloadLink: String? = null,
   var appUUID: String? = null

): Serializable