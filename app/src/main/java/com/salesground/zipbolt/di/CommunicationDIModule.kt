package com.salesground.zipbolt.di

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.implementation.MediaTransferProtocolImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
abstract class CommunicationDIModule {


    @Binds
    abstract fun getMediaTransferProtocol(
        mediaTransferProtocolImpl:
        MediaTransferProtocolImpl
    ): MediaTransferProtocol
}