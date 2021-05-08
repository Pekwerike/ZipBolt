package com.salesground.zipbolt.di

import com.salesground.zipbolt.communicationprotocol.MediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.implementation.AdvanceMediaTransferProtocol
import com.salesground.zipbolt.communicationprotocol.implementation.MediaTransferProtocolForNonJavaServers
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CommunicationDIModule::class]
)
abstract class TestCommunicationDIModule {

    @Binds
    abstract fun getMediaTransferProtocol(
        advanceMediaTransferProtocol: MediaTransferProtocolForNonJavaServers
    ): MediaTransferProtocol
}