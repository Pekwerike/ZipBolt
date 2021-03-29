package com.salesground.zipbolt.ui.screen

import android.net.wifi.p2p.WifiP2pDevice
import android.widget.LinearLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ZipBoltEntryPointLayoutBinding
import com.salesground.zipbolt.ui.screen.allmediadisplay.AllMediaOnDevice
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.AllMediaOnDeviceComposable
import com.salesground.zipbolt.ui.screen.generalcomponents.*
import com.salesground.zipbolt.ui.screen.homescreen.HomeScreen
import com.salesground.zipbolt.viewmodel.HomeScreenViewModel
import com.salesground.zipbolt.viewmodel.ImagesViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


sealed class ConnectivityState() {
    object NoAction : ConnectivityState()
    object PeersDiscoveryInitiated : ConnectivityState()
    data class PeersDiscovered(val peersList: MutableList<WifiP2pDevice>) : ConnectivityState()
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun UIEntryPoint(
    beginPeerDiscovery: () -> Unit,
    supportFragmentManager: FragmentManager,
    imagesViewModel : ImagesViewModel,
    viewPagerAdapterLifecycle: Lifecycle
) {
    lateinit var persistentBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var persistentBottomSheetState by remember { mutableStateOf(BottomSheetBehavior.STATE_HIDDEN) }
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var connectivityState: ConnectivityState by remember { mutableStateOf(ConnectivityState.NoAction) }

    var persistentBottomSheetSlideValue by remember { mutableStateOf(0f) }

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
                        (context.resources.displayMetrics.widthPixels * 0.125f).roundToInt()
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
                       /* AllMediaOnDevice(
                            supportFragmentManager =
                            supportFragmentManager, viewPagerAdapterLifecycle =
                            viewPagerAdapterLifecycle
                        )*/
                    AllMediaOnDeviceComposable(imagesViewModel = imagesViewModel)
                }
            }

            persistentBottomSheetBehavior =
                BottomSheetBehavior.from(zipBoltPersistentBottomSheetViewGroup)
            persistentBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            persistentBottomSheetBehavior.addBottomSheetCallback(
                PersistentBottomSheetCallBack(
                    actionCallback = object : PersistentBottomSheetCallBack.OnActionCallback {
                        override fun bottomSheetStateChanged(state: Int) {
                            persistentBottomSheetState = state
                            persistentBottomSheetBehavior.state = state
                        }

                        override fun bottomSheetSlide(slideValue: Float) {
                            persistentBottomSheetSlideValue = slideValue
                        }

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
                    when (connectivityState) {
                        is ConnectivityState.NoAction -> {
                        }
                        is ConnectivityState.PeersDiscoveryInitiated -> {
                            when (persistentBottomSheetState) {
                                BottomSheetBehavior.STATE_EXPANDED -> {
                                    ExpandedSearchingForPeers(onStopSearchingClicked = { },
                                        onArrowDownClicked = {})
                                }
                                BottomSheetBehavior.STATE_COLLAPSED -> {
                                    CollapsedSearchingForPeers(
                                        onCancel = {
                                            /*TODO
                1. Cancel searching for peers
                 2. Set the peek height of the bottom sheet to 0
                 3. set the bottom sheet state to hidden*/

                                        },
                                        onClick = {
                                            persistentBottomSheetBehavior.state =
                                                BottomSheetBehavior.STATE_EXPANDED
                                        })
                                }
                                else -> {
                                    // show both expanded searching for peers layout and
                                    // collapsed searching for peers layout but control their visibility using alpha
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        CollapsedSearchingForPeers(
                                            onCancel = { /*TODO*/ },
                                            onClick = {},
                                            alpha = 1 - (persistentBottomSheetSlideValue * 2.5f)
                                        )
                                        ExpandedSearchingForPeers(
                                            onStopSearchingClicked = { },
                                            onArrowDownClicked = {},
                                            alpha = persistentBottomSheetSlideValue
                                        )
                                    }
                                }
                            }
                        }
                        is ConnectivityState.PeersDiscovered -> {
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedSearchingForPeers(
    alpha: Float = 1f, onStopSearchingClicked: () -> Unit,
    onArrowDownClicked: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = onArrowDownClicked) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_down),
                            "Collapse searching for peers screen"
                        )
                    }
                    Text(
                        text = "Searching for Peers", style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                SearchingForPeersAnimation(
                    modifier = Modifier.padding(16.dp),
                    circlePeekRadius = context.resources.displayMetrics.widthPixels * 0.30f)

            }


            item {
                Text(
                    text = "Discovered Peers",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // TODO show list of discovered peers here
            items(50) {

            }
        }
        OutlinedButton(
            onClick = { /*TODO*/ }, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), 
        border = BorderStroke(width = 1.5.dp, brush = Brush.linearGradient(listOf(
            MaterialTheme.colors.primary.copy(0.6f), MaterialTheme.colors.primary.copy(0.3f)
        ))))
         {
            Text(text = "Stop Search",  modifier = Modifier
                .padding(8.dp))
        }
    }
}

@Composable
fun CollapsedSearchingForPeers(
    alpha: Float = 1f, onCancel: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SearchingForPeersAnimation(
                modifier = Modifier.padding(4.dp),
                circlePeekRadius =
                context.resources.displayMetrics.widthPixels * 0.05f,
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
