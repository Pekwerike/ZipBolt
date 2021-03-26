package com.salesground.zipbolt.ui.screen

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salesground.zipbolt.databinding.ZipBoltEntryPointLayoutBinding
import com.salesground.zipbolt.ui.screen.generalcomponents.ZipBoltMainFloatingActionButton

@ExperimentalMaterialApi
@Composable
fun UIEntryPoint() {

    var bottomSheetState by remember { mutableStateOf(BottomSheetBehavior.STATE_HIDDEN) }

    ModalBottomSheetLayout(sheetContent = {
        // TODO Display the various categories for connection
        /* The only peer connection supported now is for android devices
        when Connect to android is clicked, do the following
        1. Expand the bottomSheet to show searching for peers screen
        2. Update the mutableState for the bottom sheet with the new expanded state
        3. Update the bottom sheet peek height to 70.dp
        **/
    }) {
        AndroidViewBinding(ZipBoltEntryPointLayoutBinding::inflate) {
            zipBoltEntryPointComposeView.setContent {
                Scaffold(
                    floatingActionButton = {
                        // TODO
                        /* 1. if bottom sheet is hidden show connect button
                        2. if bottom sheet is collapsed show send button
                        2b. increase the padding of the send button by the bottom sheet peek height
                        * */
                        if(bottomSheetState == BottomSheetBehavior.STATE_HIDDEN){
                            ZipBoltMainFloatingActionButton(
                                label = "Connect",
                                onClick = { }
                            )
                        }else{
                            ZipBoltMainFloatingActionButton(
                                modifier = Modifier.padding(50.dp),
                                label = "Send",
                                icon = Icons.Rounded.Send,
                                onClick = { }
                            )
                        }
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
                    // for each state change, update the bottomSheet mutableState variable
                    when(newState){
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetState = BottomSheetBehavior.STATE_EXPANDED
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            bottomSheetState = BottomSheetBehavior.STATE_HIDDEN
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            bottomSheetState = BottomSheetBehavior.STATE_HALF_EXPANDED
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            bottomSheetState = BottomSheetBehavior.STATE_DRAGGING
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            bottomSheetState = BottomSheetBehavior.STATE_SETTLING
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_SETTLING
                        }
                    }
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

