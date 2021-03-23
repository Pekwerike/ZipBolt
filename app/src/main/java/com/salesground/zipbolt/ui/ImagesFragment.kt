package com.salesground.zipbolt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.salesground.zipbolt.R
import com.salesground.zipbolt.ui.screen.categorycontentsdisplay.images.DeviceImagesDisplayComposable
import com.salesground.zipbolt.ui.theme.ZipBoltTheme
import com.salesground.zipbolt.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImagesFragment : Fragment() {

    private val imagesViewModel: ImagesViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                ZipBoltTheme() {
                    Surface(color = MaterialTheme.colors.background) {
                        DeviceImagesDisplayComposable(imagesViewModel = imagesViewModel)
                    }
                }
            }
        }
    }


}