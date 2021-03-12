package com.salesground.zipbolt.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class MainDIModule{

    @Provides
    @Singleton
    fun getNotificationManager(@ApplicationContext context: Context) : NotificationManager{
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}