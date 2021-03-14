package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.repository.DeviceApplicationsRepository
import com.salesground.zipbolt.repository.ImageRepository
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val applicationsRepository: DeviceApplicationsRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private var _homeScreenData = MutableLiveData<MutableList<HomeScreenRecyclerviewDataModel>>()
    val homeScreenData: LiveData<MutableList<HomeScreenRecyclerviewDataModel>> = _homeScreenData

    init {
        getHomeScreenData()
    }

  private fun getHomeScreenData() {
        viewModelScope.launch {
            val allApplicationsOnDevice = async(Dispatchers.IO) {
                applicationsRepository.getNonSystemAppsOnDevice()
            }.await()
            val allImagesOnDevice = async(Dispatchers.IO) {
                imageRepository.fetchAllImagesOnDeviceOnce()
            }.await()

            _homeScreenData.value = mutableListOf<HomeScreenRecyclerviewDataModel>(
                HomeScreenRecyclerviewDataModel(dataCategory = "Apps",
                    mediaCollection = allApplicationsOnDevice.map {
                        DataCategory.Application(it)
                    }.take(10)
                ),
                HomeScreenRecyclerviewDataModel(dataCategory = "Images",
                    mediaCollection = allImagesOnDevice.map {
                        DataCategory.Image(it)
                    }.take(8))
            )
        }
    }

}