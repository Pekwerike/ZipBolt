package com.salesground.zipbolt.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.dto.ImagesDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class DeviceMediaViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val deviceImagesAsImageDisplayModel: MutableList<ImagesDisplayModel> = mutableListOf()

    private var _deviceImagesGroupedByDateModified =
        MutableLiveData<MutableList<ImagesDisplayModel>>()
    val deviceImagesGroupedByDateModified: LiveData<MutableList<ImagesDisplayModel>> =
        _deviceImagesGroupedByDateModified

    init {
        transformDeviceImagesToImagesDisplayModel()
    }

    fun filterDeviceImagesByBucketName(bucketName: String){

    }

    private fun transformDeviceImagesToImagesDisplayModel() {
        viewModelScope.launch {
            val allImagesOnDevice =
                imageRepository.getImagesOnDevice() as MutableList<DataToTransfer.DeviceImage>
            allImagesOnDevice.groupBy {
                it.imageDateModified
            }.forEach { (header, deviceImages) ->
                deviceImagesAsImageDisplayModel.add(ImagesDisplayModel.ImagesDateModifiedHeader(dateModified = header))
                deviceImagesAsImageDisplayModel.addAll(deviceImages.map {
                    ImagesDisplayModel.DeviceImageDisplay(it)
                })
            }
            withContext(Dispatchers.Main) {
                _deviceImagesGroupedByDateModified.value = deviceImagesAsImageDisplayModel
            }
        }
    }
}