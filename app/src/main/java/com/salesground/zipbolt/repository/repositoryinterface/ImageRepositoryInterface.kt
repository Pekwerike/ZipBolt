package com.salesground.zipbolt.repository.repositoryinterface

import com.salesground.zipbolt.model.MediaModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ImageRepositoryInterface {
    fun fetchAllImagesOnDevice(): Flow<MediaModel>
    fun convertImageModelToFile(imagesToConvert: MutableList<MediaModel>): MutableList<File>
}