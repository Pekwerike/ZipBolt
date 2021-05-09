package com.salesground.zipbolt.di

import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.communication.implementation.MediaTransferProtocolImpl
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
        mediaTransferProtocolImpl: MediaTransferProtocolImpl
    ): MediaTransferProtocol
}