package com.salesground.zipbolt.ui.screen

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.view.View
import android.widget.LinearLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salesground.zipbolt.databinding.ZipBoltEntryPointLayoutBinding
import com.salesground.zipbolt.ui.screen.generalcomponents.*
import com.salesground.zipbolt.ui.screen.homescreen.HomeScreen
import com.salesground.zipbolt.viewmodel.HomeScreenViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


sealed class ConnectivityState() {
    object NoAction : ConnectivityState()
    object PeersDiscoveryInitiated : ConnectivityState()
    data class PeersDiscovered(val peersList: MutableList<WifiP2pDevice>) : ConnectivityState()
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun UIEntryPoint(
    beginPeerDiscovery: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel
) {
    lateinit var persistentBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var persistentBottomSheetState by remember { mutableStateOf(BottomSheetBehavior.STATE_HIDDEN) }
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var connectivityState : ConnectivityState by remember { mutableStateOf(ConnectivityState.NoAction) }

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
                    connectivityState = ConnectivityState.PeersDiscoveryInitiated

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
                        (context.resources.displayMetrics.widthPixels * 0.15f).roundToInt()
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
                            /*ZipBoltMainFloatingActionButton(
                                modifier = Modifier.padding(bottom = 50.dp),
                                label = "Send",
                                icon = Icons.Rounded.Send,
                                onClick = { }
                            )*/
                        }
                    }
                ) {
                    // Place navHost here
                    HomeScreen(homeScreenViewModel = homeScreenViewModel)
                }
            }

            persistentBottomSheetBehavior =
                BottomSheetBehavior.from(zipBoltPersistentBottomSheetViewGroup)
            persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            persistentBottomSheetBehavior.addBottomSheetCallback(
                PersistentBottomSheetCallBack(
                    persistentBottomSheetStateChanged = { newState ->
                        persistentBottomSheetState = newState
                        persistentBottomSheetBehavior.state = newState
                    }
                )
            )

            zipBoltPersistentBottomSheetComposeView.setContent {
                // place different bottom sheet contents here
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.surface)
                ) {
                    when(connectivityState){
                        is ConnectivityState.NoAction ->{}
                        is ConnectivityState.PeersDiscoveryInitiated -> {
                            when(persistentBottomSheetState){
                              BottomSheetBehavior.STATE_EXPANDED -> { }
                              BottomSheetBehavior.STATE_COLLAPSED -> {
                                  AnimatedVisibility(visible = true, initiallyVisible = false) {
                                      CollapsedSearchingForPeers(
                                          onCancel = {
                                               /*TODO
                   1. Cancel searching for peers
                    2. Set the peek height of the bottom sheet to 0
                    3. set the bottom sheet state to hidden*/

                                          }
                                      ) {
                                          persistentBottomSheetBehavior.state =
                                              BottomSheetBehavior.STATE_EXPANDED
                                      }
                                  }
                              }
                              else ->  {
                                  // show both expanded searching for peers layout and
                                  // collapsed searching for peers layout but control their visibility using alpha

                              }
                            }
                        }
                        is ConnectivityState.PeersDiscovered -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun

@Composable
fun CollapsedSearchingForPeers(alpha : Float = 1f, onCancel : () -> Unit,
                               expand: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clickable { expand() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SearchingForPeersAnimation(
                circlePeekRadius =
                context.resources.displayMetrics.widthPixels * 0.05f
            )

            Column() {
                Text(
                    text = "Searching for peers",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "0 devices found",
                    style = MaterialTheme.typography.caption
                )
            }
        }
        IconButton(
            onClick = onCancel
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "")
        }
    }
}
