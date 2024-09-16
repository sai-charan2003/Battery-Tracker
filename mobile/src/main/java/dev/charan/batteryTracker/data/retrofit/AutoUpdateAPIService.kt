package dev.charan.batteryTracker.data.retrofit

import dev.charan.batteryTracker.data.model.AutoUpdateDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AutoUpdateAPIService {

    @GET("/getData")
    suspend fun fetchLatestAppVersion(
        @Query("apiKey") apiKey: String = dev.charan.batteryTracker.BuildConfig.API_KEY,
        @Query("appName") appName : String
    ) : Response<AutoUpdateDTO>
}