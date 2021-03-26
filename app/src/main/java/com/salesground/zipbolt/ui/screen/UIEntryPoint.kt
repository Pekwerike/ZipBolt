package com.salesground.zipbolt.ui.screen

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salesground.zipbolt.databinding.ZipBoltEntryPointLayoutBinding
import com.salesground.zipbolt.ui.screen.generalcomponents.ZipBoltMainFloatingActionButton
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun UIEntryPoint() {

    val coroutineScope = rememberCoroutineScope()
    var persistentBottomSheetState by remember { mutableStateOf(BottomSheetBehavior.STATE_HIDDEN) }
    var modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(sheetContent = {
                                          
        // TODO Display the various categories for connection
        /* The only peer connection supported now is for android devices
        when Connect to android is clicked, do the following
        1. Expand the bottomSheet to show searching for peers screen
        2. Update the mutableState for the bottom sheet with the new expanded state
        3. Update the bottom sheet peek height to 70.dp
        **/
        Column(modifier = Modifier.clip(shape = MaterialTheme.shapes.large.copy(
            topStart = CornerSize(10.dp),
            topEnd = CornerSize(10.dp)
        ))) {
            Surface(modifier = Modifier.padding(8.dp)) {
                
            }
        }                                  

    }, sheetState = modalBottomSheetState) {
        AndroidViewBinding(ZipBoltEntryPointLayoutBinding::inflate) {
            zipBoltEntryPointComposeView.setContent {
                Scaffold(
                    floatingActionButton = {
                        // TODO
                        /* 1. if bottom sheet is hidden show connect button
                        2. if bottom sheet is collapsed show send button
                        2b. increase the padding of the send button by the bottom sheet peek height
                        3. OnClick of the connect button, open the modal bottom sheet action options
                        * */
                        if (persistentBottomSheetState == BottomSheetBehavior.STATE_HIDDEN) {
                            ZipBoltMainFloatingActionButton(
                                label = "Connect",
                                onClick = {
                                    coroutineScope.launch {
                                        modalBottomSheetState.show()
                                    }
                                }
                            )
                        } else {
                            ZipBoltMainFloatingActionButton(
                                modifier = Modifier.padding(bottom = 50.dp),
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
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            persistentBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            persistentBottomSheetState = BottomSheetBehavior.STATE_EXPANDED
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            persistentBottomSheetState = BottomSheetBehavior.STATE_HIDDEN
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            persistentBottomSheetState = BottomSheetBehavior.STATE_HALF_EXPANDED
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            persistentBottomSheetState = BottomSheetBehavior.STATE_DRAGGING
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            persistentBottomSheetState = BottomSheetBehavior.STATE_SETTLING
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

