package com.salesground.zipbolt.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.repository.AudioRepository
import com.salesground.zipbolt.repository.DeviceApplicationsRepository
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.repository.VideoRepository
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.ImagesDisplayModel
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val applicationsRepository: DeviceApplicationsRepository,
    private val imageRepository: ImageRepository,
    private val videoRepository: VideoRepository,
    private val audioRepository: AudioRepository
) : ViewModel() {

    private var _homeScreenData = MutableLiveData<MutableList<HomeScreenRecyclerviewDataModel>>()
    val homeScreenData: LiveData<MutableList<HomeScreenRecyclerviewDataModel>> = _homeScreenData

    private var _deviceImages = MutableLiveData<List<MediaModel>>()
    val deviceImages: LiveData<List<MediaModel>> = _deviceImages

    private var _deviceVideos = MutableLiveData<List<MediaModel>>()
    val deviceVideos: LiveData<List<MediaModel>> = _deviceVideos

    private var _deviceApplications = MutableLiveData<List<ApplicationModel>>()
    val deviceApplications: LiveData<List<ApplicationModel>> = _deviceApplications

    private var _deviceAudio = MutableLiveData<List<MediaModel>>()
    val deviceAudio : LiveData<List<MediaModel>> = _deviceAudio

    init {
      //  getHomeScreenData()
        getHomeScreenDataTwo()
    }

    private fun transformDeviceImagesToImagesDisplayModel()
    : MutableList<ImagesDisplayModel>{
        val imagesModelForImageDetailsRecyclerView = mutableListOf<ImagesDisplayModel>()
        val allImagesOnDevice = imageRepository.fetchAllImagesOnDeviceOnce()
        allImagesOnDevice.groupBy {
            // todo convert mediaDateAdded to string date, that will be used to group images
            it.mediaDateAdded
        }.forEach { (header, deviceImages) ->
            imagesModelForImageDetailsRecyclerView
                .add(ImagesDisplayModel.ImagesDateModifiedHeader(dateModified = header.toString()))

            deviceImages.forEach {
                imagesModelForImageDetailsRecyclerView.add(
                    ImagesDisplayModel.DeviceImageDisplay(it))
            }
        }
        return imagesModelForImageDetailsRecyclerView
    }

    private fun getHomeScreenDataTwo() {
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                val nonSystemAppsOnDevice =
                    applicationsRepository.getNonSystemAppsOnDevice().take(10)
                withContext(Dispatchers.Main) {
                    _deviceApplications.value = nonSystemAppsOnDevice
                }
            }
            launch(Dispatchers.IO) {
                val deviceImages = imageRepository.fetchAllImagesOnDevicePreviewList()
                withContext(Dispatchers.Main) {
                    _deviceImages.value = deviceImages
                }
            }
            launch(Dispatchers.IO) {
                val deviceVideos = videoRepository.getAllVideoFromDevicePreviewList()
                withContext(Dispatchers.Main) {
                    _deviceVideos.value = deviceVideos
                }
            }
            launch(Dispatchers.IO) {
                val deviceAudio = audioRepository.getAudioPreviewList()
                withContext(Dispatchers.Main){
                    _deviceAudio.value = deviceAudio
                }
            }
        }
    }

}