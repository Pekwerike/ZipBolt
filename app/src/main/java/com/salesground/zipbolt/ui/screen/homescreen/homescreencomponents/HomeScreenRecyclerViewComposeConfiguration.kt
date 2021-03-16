package com.salesground.zipbolt.ui.screen.homescreen.homescreencomponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.salesground.zipbolt.databinding.HomeScreenRecyclerViewBinding
import com.salesground.zipbolt.model.ApplicationModel
import com.salesground.zipbolt.model.MediaModel
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HomeScreenRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel

@Composable
fun HomeScreenRecyclerViewComposeConfiguration(
    deviceApplication: List<ApplicationModel>,
    deviceImages: List<MediaModel>,
    deviceVideos: List<MediaModel>,
    deviceAudio: List<MediaModel>
) {
    AndroidViewBinding(factory = HomeScreenRecyclerViewBinding::inflate) {
        val hSRAdapter = HomeScreenRecyclerViewAdapter()
        hSRAdapter.submitList(
            mutableListOf(
                HomeScreenRecyclerviewDataModel("Apps",
                    deviceApplication.map {
                        DataCategory.Application(it)
                    }),
                HomeScreenRecyclerviewDataModel("Images",
                    deviceImages.map {
                        DataCategory.Image(it)
                    }),
                HomeScreenRecyclerviewDataModel("Videos",
                    deviceVideos.map {
                        DataCategory.Video(it)
                    }),
                HomeScreenRecyclerviewDataModel("Music",
                    deviceAudio.map {
                        DataCategory.Music(it)
                    })
            )
        )
        homeScreenRecyclerView.adapter = hSRAdapter
    }
}