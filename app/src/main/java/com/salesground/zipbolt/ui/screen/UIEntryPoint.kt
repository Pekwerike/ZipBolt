package com.salesground.zipbolt.ui.screen

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salesground.zipbolt.databinding.ZipBoltEntryPointLayoutBinding

@ExperimentalMaterialApi
@Composable
fun UIEntryPoint() {

    ModalBottomSheetLayout(sheetContent = {
        // TODO Display the various categories for connection
    }) {
        AndroidViewBinding(ZipBoltEntryPointLayoutBinding::inflate) {
            zipBoltEntryPointComposeView.setContent {
                Scaffold(
                    floatingActionButton = {

                    }
                ) {
                    // Place navHost here
                }
            }

            val bottomSheetBehavior =
                BottomSheetBehavior.from(zipBoltPersistentBottomSheetViewGroup)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

            })

            zipBoltPersistentBottomSheetComposeView.setContent {
                // place different bottom sheet contents here
            }
        }
    }
}

