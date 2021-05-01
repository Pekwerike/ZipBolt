package com.salesground.zipbolt.di

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WifiP2pDIModule {

    @Provides
    @Singleton
    fun getWifiP2pManager(@ApplicationContext context : Context) : WifiP2pManager{
        return context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
}