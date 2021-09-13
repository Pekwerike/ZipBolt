package com.salesground.zipbolt.viewmodel


import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.model.ui.ImagesDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private var allImagesOnDeviceRaw: MutableList<DataToTransfer.DeviceImage> = mutableListOf()

    private var _deviceImagesBucketNames = MutableLiveData<MutableList<BucketNameAndSize>>()
    val deviceImagesBucketName: LiveData<MutableList<BucketNameAndSize>> = _deviceImagesBucketNames

    private var _deviceImagesGroupedByDateModified =
        MutableLiveData<MutableList<ImagesDisplayModel>>()
    val deviceImagesGroupedByDateModified: LiveData<MutableList<ImagesDisplayModel>> =
        _deviceImagesGroupedByDateModified

    private var _chosenBucket: Pair<String, Int> = Pair("All", 1)
    val chosenBucket: Pair<String, Int>
        get() = _chosenBucket



    fun getAllImages(){
        viewModelScope.launch(Dispatchers.IO) {
            allImagesOnDeviceRaw =
                imageRepository.getImagesOnDevice() as MutableList<DataToTransfer.DeviceImage>
            filterDeviceImages(chipId = 1)
            val imageBucketNames = getDeviceImagesBucketNames(allImagesOnDeviceRaw)
            withContext(Dispatchers.Main) {
                _deviceImagesBucketNames.value = imageBucketNames
            }
        }
    }


    private fun getDeviceImagesBucketNames(allImagesOnDevice: MutableList<DataToTransfer.DeviceImage>)
            : MutableList<BucketNameAndSize> {
        val listOfArrangedBuckets: MutableList<BucketNameAndSize> = mutableListOf()
        val deviceImagesBucketNames: HashMap<String, Int> = hashMapOf()

        deviceImagesBucketNames["All"] = allImagesOnDevice.size
        allImagesOnDevice.forEach {
            deviceImagesBucketNames[it.imageBucketName] =
                deviceImagesBucketNames.getOrPut(it.imageBucketName, { 0 }) + 1
        }

        deviceImagesBucketNames.forEach { (s, i) ->
            listOfArrangedBuckets.add(BucketNameAndSize(bucketName = s, bucketSize = i))
        }
        listOfArrangedBuckets.sortByDescending {
            it.bucketSize
        }
        return listOfArrangedBuckets
    }


    suspend fun filterDeviceImages(
        bucketName: String = "All",
        chipId: Int
    ) {
        if (bucketName != "All" && bucketName == _chosenBucket.first) return

        withContext(Dispatchers.Main) {
            _chosenBucket = Pair(bucketName, chipId)
        }
        if (bucketName == "All") {
            // don't filter
            val allImages =
                allDeviceImagesToImagesDisplayModel(allImagesOnDevice = allImagesOnDeviceRaw)
            withContext(Dispatchers.Main) {
                _deviceImagesGroupedByDateModified.value = allImages
            }

        } else {
            // filter
            val filteredImages = filterDeviceImagesByBucketName(
                allImagesOnDevice = allImagesOnDeviceRaw,
                bucketName = bucketName
            )
            withContext(Dispatchers.Main) {
                _deviceImagesGroupedByDateModified.value = filteredImages
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

data class BucketNameAndSize(val bucketName: String, val bucketSize: Int)