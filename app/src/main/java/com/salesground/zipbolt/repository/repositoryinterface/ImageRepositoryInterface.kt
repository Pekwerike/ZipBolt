package com.salesground.zipbolt.repository.repositoryinterface

import com.salesground.zipbolt.model.MediaModel
import java.io.File

interface IImageRepositoryInterface {
    fun fetchAllImagesOnDevice(): MutableList<MediaModel>
    fun convertImageModelToFile(imagesToConvert: MutableList<MediaModel>): MutableList<File>
}