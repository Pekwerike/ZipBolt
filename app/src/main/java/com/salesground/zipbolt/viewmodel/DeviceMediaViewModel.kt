package com.salesground.zipbolt.viewmodel


import android.util.ArrayMap
import android.util.SparseArray
import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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

    private var allImagesOnDeviceRaw: MutableList<DataToTransfer.DeviceImage> = mutableListOf()

    private var _deviceImagesBucketNames = MutableLiveData<HashMap<String, Int>>()
    val deviceImagesBucketName: LiveData<HashMap<String, Int>> = _deviceImagesBucketNames

    private var _deviceImagesGroupedByDateModified =
        MutableLiveData<MutableList<ImagesDisplayModel>>()
    val deviceImagesGroupedByDateModified: LiveData<MutableList<ImagesDisplayModel>> =
        _deviceImagesGroupedByDateModified

    init {
        viewModelScope.launch {
            allImagesOnDeviceRaw =
                imageRepository.getImagesOnDevice() as MutableList<DataToTransfer.DeviceImage>
            launch(Dispatchers.IO) {
                _deviceImagesBucketNames.value = getDeviceImagesBucketNames(allImagesOnDeviceRaw)
            }
            filterDeviceImages()
        }
    }

    private fun getDeviceImagesBucketNames(allImagesOnDevice: MutableList<DataToTransfer.DeviceImage>):
            HashMap<String, Int> {
        val deviceImagesBucketNames: HashMap<String, Int> = hashMapOf()
        allImagesOnDevice.forEach {
            deviceImagesBucketNames[it.imageBucketName] = 1
        }
        return deviceImagesBucketNames
    }


    fun filterDeviceImages(bucketName: String = "All") {
        viewModelScope.launch {
            if (bucketName == "All") {
                // don't filter
                _deviceImagesGroupedByDateModified.value =
                    withContext(Dispatchers.IO) {
                        allDeviceImagesToImagesDisplayModel(allImagesOnDevice = allImagesOnDeviceRaw)
                    }
            } else {
                // filter
                _deviceImagesGroupedByDateModified.value = withContext(Dispatchers.IO) {
                    filterDeviceImagesByBucketName(
                        allImagesOnDevice = allImagesOnDeviceRaw,
                        bucketName = bucketName
                    )
                }
            }
        }
    }

    private fun allDeviceImagesToImagesDisplayModel(allImagesOnDevice: MutableList<DataToTransfer.DeviceImage>)
            : MutableList<ImagesDisplayModel> {
        val deviceImagesReadyAsImageDisplayModel: MutableList<ImagesDisplayModel> = mutableListOf()
        allImagesOnDevice.groupBy {
            it.imageDateModified
        }.forEach { (header, deviceImages) ->
            deviceImagesReadyAsImageDisplayModel.add(
                ImagesDisplayModel.ImagesDateModifiedHeader(
                    dateModified = header
                )
            )
            deviceImagesReadyAsImageDisplayModel.addAll(deviceImages.map {
                ImagesDisplayModel.DeviceImageDisplay(it)
            })
        }
        return deviceImagesReadyAsImageDisplayModel
    }

    private fun filterDeviceImagesByBucketName(
        allImagesOnDevice: MutableList<DataToTransfer.DeviceImage>,
        bucketName: String
    ): MutableList<ImagesDisplayModel> {
        val deviceImagesReadyAsImageDisplayModel: MutableList<ImagesDisplayModel> = mutableListOf()
        allImagesOnDevice.filter {
            it.imageBucketName == bucketName
        }.groupBy {
            it.imageDateModified
        }.forEach { (header, deviceImages) ->

            deviceImagesReadyAsImageDisplayModel.add(
                ImagesDisplayModel.ImagesDateModifiedHeader(
                    dateModified = header
                )
            )
            deviceImagesReadyAsImageDisplayModel.addAll(deviceImages.map {
                ImagesDisplayModel.DeviceImageDisplay(it)
            })
        }
        return deviceImagesReadyAsImageDisplayModel
    }
}