package com.salesground.zipbolt.di

import com.salesground.zipbolt.repository.*
import com.salesground.zipbolt.repository.implementation.AdvanceImageRepository
import com.salesground.zipbolt.repository.implementation.DeviceApplicationsRepository
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
    abstract fun zipBoltImageRepository(advanceImageRepository: AdvanceImageRepository): ImageRepository

    @Singleton
    @Binds
    abstract fun zipBoltSavedFilesRepository(zipBoltSavedFilesRepository: ZipBoltSavedFilesRepository): SavedFilesRepository

    @Singleton
    @Binds
    abstract fun applicationsRepository(deviceApplicationsRepository: DeviceApplicationsRepository): ApplicationsRepositoryInterface
}