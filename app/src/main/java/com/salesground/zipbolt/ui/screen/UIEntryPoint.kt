package com.salesground.zipbolt.ui.screen

import android.view.View
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salesground.zipbolt.databinding.ZipBoltEntryPointLayoutBinding
import com.salesground.zipbolt.ui.screen.generalcomponents.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@ExperimentalMaterialApi
@Composable
fun UIEntryPoint(
    beginPeerDiscovery: () -> Unit
) {
    lateinit var persistentBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var persistentBottomSheetState by remember { mutableStateOf(BottomSheetBehavior.STATE_HIDDEN) }
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


    ModalBottomSheetLayout(
        sheetContent = {
            // TODO Display the various categories for connection
            /* The only peer connection supported now is for android devices
            when Connect to android is clicked, do the following
            1. Expand the bottomSheet to show searching for peers screen
            2. Update the mutableState for the bottom sheet with the new expanded state
            3. Update the bottom sheet peek height to 70.dp
            **/
            ZipBoltModalBottomSheetContent(
                onAndroidClicked = {
                    beginPeerDiscovery()
                    /* TODO,
                    1. Close the modal bottom sheet *
                    2. Open the persistent bottom sheet in full screen *
                    3. increase the peekHeight state of the persistent bottom sheet, *
                    so that the send button can appear over the collapsed persistent bottom sheet
                    */
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                    persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    persistentBottomSheetBehavior.peekHeight =
                        (50 * context.resources.displayMetrics.scaledDensity).roundToInt()
                }
            )

        }, sheetState = modalBottomSheetState,
        sheetShape = MaterialTheme.shapes.large.copy(
            topStart = CornerSize(12.dp), topEnd = CornerSize(12.dp)
        )
    ) {
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

            persistentBottomSheetBehavior =
                BottomSheetBehavior.from(zipBoltPersistentBottomSheetViewGroup)
            persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            persistentBottomSheetBehavior.addBottomSheetCallback(
                PersistentBottomSheetCallBack(
                    persistentBottomSheetBehavior,
                    persistentBottomSheetStateChanged = {
                        persistentBottomSheetState = it
                    }
                )
            )

            zipBoltPersistentBottomSheetComposeView.setContent {
                // place different bottom sheet contents here
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red)) {

                }
            }
        }
    }
}
