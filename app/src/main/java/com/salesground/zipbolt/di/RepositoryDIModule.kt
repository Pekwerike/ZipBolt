package com.salesground.zipbolt.di

import com.salesground.zipbolt.repository.*
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import com.salesground.zipbolt.repository.implementation.DeviceApplicationsRepository
import com.salesground.zipbolt.repository.implementation.ZipBoltAudioRepository
import com.salesground.zipbolt.repository.implementation.ZipBoltVideoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryDIModule {
    @Singleton
    @Binds
    abstract fun getZipBoltImageRepository(advanceImageRepository: AdvanceImageRepository): ImageRepository

    @Singleton
    @Binds
    abstract fun getZipBoltSavedFilesRepository(zipBoltSavedFilesRepository: ZipBoltSavedFilesRepository): SavedFilesRepository

    @Singleton
    @Binds
    abstract fun getAapplicationsRepository(deviceApplicationsRepository: DeviceApplicationsRepository): ApplicationsRepositoryInterface

    @Singleton
    @Binds
    abstract fun getZipBoltVideoRepository(zipBoltVideoRepository: ZipBoltVideoRepository): VideoRepositoryI

    @Singleton
    @Binds
    abstract fun getZipBoltAudioRepository(zipBoltAudioRepository: ZipBoltAudioRepository): AudioRepositoryI
}