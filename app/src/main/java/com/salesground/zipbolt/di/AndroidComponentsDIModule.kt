package com.salesground.zipbolt.di

import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AndroidComponentsDIModule {

    @Provides
    fun getNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun getLocalBroadcastManager(@ApplicationContext context: Context): LocalBroadcastManager {
        return LocalBroadcastManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun getWifiP2pManager(@ApplicationContext context: Context): WifiP2pManager {
        return context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }

    @Provides
    @Singleton
    fun getWifiManager(@ApplicationContext context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @Provides
    @Singleton
    fun getConnectivityManager(@ApplicationContext context: Context) : ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}