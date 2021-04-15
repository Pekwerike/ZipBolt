package com.salesground.zipbolt.di

import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.ZipBoltMediaTransferProtocol
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

// for testing case change the installation component of the module
@InstallIn(ServiceComponent::class)
@Module
abstract class CommunicationDIModule {

    @Binds
    abstract fun getMediaTransferProtocol(
        zipBoltMediaTransferProtocol:
        ZipBoltMediaTransferProtocol
    ): MediaTransferProtocol
}