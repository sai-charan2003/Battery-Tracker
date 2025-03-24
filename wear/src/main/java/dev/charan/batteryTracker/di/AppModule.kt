package dev.charan.batteryTracker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.charan.batteryTracker.ListenerService
import dev.charan.batteryTracker.data.repository.BatteryInfoRepo
import dev.charan.batteryTracker.data.repository.impl.BatteryInfoRepoImp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBatteryInfoRepo(@ApplicationContext context: Context) : BatteryInfoRepo {
        return BatteryInfoRepoImp(context)
    }

}