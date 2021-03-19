package com.salesground.zipbolt.di

import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.implementation.ZipBoltImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryDIModule {
    @Binds
    abstract fun zipBoltImageRepository(zipBoltImageRepository: ZipBoltImageRepository): ImageRepository
}