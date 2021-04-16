package com.salesground.zipbolt.di

import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.ZipBoltMediaTransferProtocol
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CommunicationDIModule::class]
)
abstract class TestCommunicationDIModule {

    @Binds
    abstract fun getMediaTransferProtocol(
        zipBoltMediaTransferProtocol:
        ZipBoltMediaTransferProtocol
    ): MediaTransferProtocol
}