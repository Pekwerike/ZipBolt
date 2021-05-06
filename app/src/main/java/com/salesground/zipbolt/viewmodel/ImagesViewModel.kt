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
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private var allImagesOnDeviceRaw: MutableList<DataToTransfer.DeviceImage> = mutableListOf()

    private val _collectionOfClickedImages =
        MutableLiveData<ArrayMap<ImagesDisplayModel, Boolean>>(ArrayMap())
    val collectionOfClickedImages: LiveData<ArrayMap<ImagesDisplayModel, Boolean>> =
        _collectionOfClickedImages

    private var _deviceImagesBucketNames = MutableLiveData<MutableList<BucketNameAndSize>>()
    val deviceImagesBucketName: LiveData<MutableList<BucketNameAndSize>> = _deviceImagesBucketNames

    private var _deviceImagesGroupedByDateModified =
        MutableLiveData<MutableList<ImagesDisplayModel>>()
    val deviceImagesGroupedByDateModified: LiveData<MutableList<ImagesDisplayModel>> =
        _deviceImagesGroupedByDateModified

    private var _chosenBucket = MutableLiveData<String>()
    val chosenBucket: LiveData<String> = _chosenBucket


    init {
        viewModelScope.launch {
            allImagesOnDeviceRaw =
                imageRepository.getImagesOnDevice() as MutableList<DataToTransfer.DeviceImage>
            filterDeviceImages()
            launch(Dispatchers.IO) {
                val imageBucketNames = getDeviceImagesBucketNames(allImagesOnDeviceRaw)
                withContext(Dispatchers.Main) {
                    _deviceImagesBucketNames.value = imageBucketNames
                }
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


    fun filterDeviceImages(bucketName: String = "All") {
        if (bucketName != "All" && bucketName == _chosenBucket.value) return
        _chosenBucket.value = bucketName

        viewModelScope.launch {
            if (bucketName == "All") {
                // don't filter
                _deviceImagesGroupedByDateModified.value =
                    withContext(Dispatchers.IO) {
                        allDeviceImagesToImagesDisplayModel(allImagesOnDevice = allImagesOnDeviceRaw)
                    }!!
            } else {
                // filter
                _deviceImagesGroupedByDateModified.value = withContext(Dispatchers.IO) {
                    filterDeviceImagesByBucketName(
                        allImagesOnDevice = allImagesOnDeviceRaw,
                        bucketName = bucketName
                    )
                }!!
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


    fun onImageClicked(imageClicked: ImagesDisplayModel) {
        if (_collectionOfClickedImages.value!!.containsKey(imageClicked)) {
            // un clicked
            _collectionOfClickedImages.value!!.remove(imageClicked)
        } else {
            // clicked
            _collectionOfClickedImages.value!![imageClicked] = true
        }
    }
}

data class BucketNameAndSize(val bucketName: String, val bucketSize: Int)