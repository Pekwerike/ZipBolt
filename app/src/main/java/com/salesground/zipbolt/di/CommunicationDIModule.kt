package com.salesground.zipbolt.di

import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.implementation.AdvanceMediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.implementation.MediaTransferProtocolForNonJavaServers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(ServiceComponent::class)
@Module
abstract class CommunicationDIModule {


    @Binds
    abstract fun getMediaTransferProtocol(
        advanceMediaTransferProtocol:
        MediaTransferProtocolForNonJavaServers
    ): MediaTransferProtocol
}