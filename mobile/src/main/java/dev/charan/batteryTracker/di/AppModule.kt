package dev.charan.batteryTracker.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.charan.batteryTracker.utils.SettingsUtils
import dev.charan.batteryTracker.data.Repository.BatteryInfoRepo
import dev.charan.batteryTracker.data.Repository.WidgetRepository
import dev.charan.batteryTracker.data.Repository.impl.BatteryInfoRepoImp
import dev.charan.batteryTracker.data.prefs.SharedPref
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideBatteryInfoRepo(@ApplicationContext context : Context): BatteryInfoRepo {
        return BatteryInfoRepoImp(context, provideSharedPref(context))
    }

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context : Context): SharedPref {
        return SharedPref(context)
    }

    @Provides
    @Singleton
    fun provideSettingsUtils(@ApplicationContext context : Context): SettingsUtils {
        return SettingsUtils(context)
    }

    @Provides
    @Singleton
    fun provideWidgetRepository(@ApplicationContext context : Context, batteryInfoRepo: BatteryInfoRepo, sharedPref: SharedPref): WidgetRepository {
        return WidgetRepository(
            context = context,
            batteryInfoRepo = batteryInfoRepo,
            sharedPref = sharedPref
        )
    }
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

}