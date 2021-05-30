package com.salesground.zipbolt


import android.Manifest.*
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animate
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.salesground.zipbolt.broadcast.IncomingDataBroadcastReceiver
import com.salesground.zipbolt.broadcast.WifiDirectBroadcastReceiver
import com.salesground.zipbolt.broadcast.WifiDirectBroadcastReceiver.WifiDirectBroadcastReceiverCallback
import com.salesground.zipbolt.communication.MediaTransferProtocol
import com.salesground.zipbolt.databinding.*
import com.salesground.zipbolt.databinding.ActivityMainBinding.inflate
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.DiscoveredPeersDataItem
import com.salesground.zipbolt.model.ui.OngoingDataTransferUIState
import com.salesground.zipbolt.model.ui.PeerConnectionUIState

import com.salesground.zipbolt.notification.FileTransferServiceNotification
import com.salesground.zipbolt.service.DataTransferService
import com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation.DiscoveredPeersRecyclerViewAdapter
import com.salesground.zipbolt.ui.AllMediaOnDeviceViewPager2Adapter
import com.salesground.zipbolt.ui.recyclerview.expandedconnectedtopeertransferongoing.ExpandedConnectedToPeerTransferOngoingRecyclerviewAdapter
import com.salesground.zipbolt.ui.recyclerview.imagefragment.DeviceImagesDisplayViewHolderType
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.OngoingDataTransferRecyclerViewAdapter
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.OngoingDataTransferRecyclerViewAdapter.*
import com.salesground.zipbolt.utils.customizeDate
import com.salesground.zipbolt.utils.parseDate
import com.salesground.zipbolt.utils.transformDataSizeToMeasuredUnit
import com.salesground.zipbolt.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt


private const val FINE_LOCATION_REQUEST_CODE = 100
const val OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE = 1010
const val SERVER_IP_ADDRESS_KEY = "ServerIpAddress"
const val IS_SERVER_KEY = "IsDeviceServer"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var ftsNotification: FileTransferServiceNotification

    @Inject
    lateinit var wifiP2pManager: WifiP2pManager

    @Inject
    lateinit var wifiManager: WifiManager

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var wifiDirectBroadcastReceiver: WifiDirectBroadcastReceiver

    private val incomingDataBroadcastReceiver: IncomingDataBroadcastReceiver by lazy {
        IncomingDataBroadcastReceiver(object : IncomingDataBroadcastReceiver.DataReceiveListener {
            override fun onDataReceive(
                dataDisplayName: String,
                dataUri: Uri?,
                dataSize: Long,
                dataType: Int,
                percentTransferred: Float,
                transferStatus: Int
            ) {
                when (transferStatus) {
                    DataToTransfer.TransferStatus.RECEIVE_STARTED.value -> {
                        when (dataType) {
                            DataToTransfer.MediaType.IMAGE.value -> {
                                Log.i("ReceiveSt", "ReceiveStarted")
                                    with(
                                        connectedToPeerTransferOngoingBottomSheetLayoutBinding
                                            .expandedConnectedToPeerTransferOngoingLayout
                                            .expandedConnectedToPeerTransferOngoingLayoutHeader
                                    ) {
                                        // hide the  no item in receive label
                                        ongoingTransferReceiveHeaderLayoutNoItemsInReceiveTextView.root.animate()
                                            .alpha(0f)
                                        with(ongoingTransferReceiveHeaderLayoutDataReceiveView) {
                                            root.animate().alpha(1f)
                                            this.dataDisplayName = dataDisplayName
                                            this.dataSize =
                                                dataSize.transformDataSizeToMeasuredUnit()
                                        }
                                    }
                                }
                            else -> {


                            }
                        }
                    }

                    DataToTransfer.TransferStatus.RECEIVE_COMPLETE.value -> {
                        when (dataType) {
                            DataToTransfer.MediaType.IMAGE.value -> {
                                mainActivityViewModel.addDataFromReceiveToUIState(
                                    DataToTransfer.DeviceImage(
                                        0L,
                                        dataUri!!,
                                        System.currentTimeMillis().parseDate()
                                            .customizeDate(),
                                        dataDisplayName,
                                        "",
                                        dataSize,
                                        ""
                                    )
                                )
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        })
    }

    // ui variables
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var modalBottomSheetDialog: BottomSheetDialog
    private var isSearchingForPeersBottomSheetLayoutConfigured: Boolean = false
    private var isConnectedToPeerNoActionBottomSheetLayoutConfigured: Boolean = false
    private var isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured: Boolean = false
    private var shouldStopPeerDiscovery: Boolean = false
    private var startPeerDiscovery: Boolean = false

    private val discoveredPeersRecyclerViewAdapter: DiscoveredPeersRecyclerViewAdapter by lazy {
        DiscoveredPeersRecyclerViewAdapter(
            connectToDeviceClickListener = object :
                DiscoveredPeersRecyclerViewAdapter.ConnectToDeviceClickListener {
                override fun onConnectToDevice(wifiP2pDevice: WifiP2pDevice) {
                    connectToADevice(wifiP2pDevice)
                    startPeerDiscovery = false
                    //   stopDevicePeerDiscovery()
                    /**
                     * 1. Collapse the searching for peers expanded bottom sheet ui
                     * 2. Display the connected to peer collapsed bottom sheet ui
                     * 3. Stop peer discovery
                     * **/
                }
            }
        )
    }

    private val ongoingDataTransferRecyclerViewAdapter = OngoingDataTransferRecyclerViewAdapter()

    private val expandedSearchingForPeersInfoBinding:
            ExpandedSearchingForPeersInformationBinding by lazy {
        MainActivityDataBindingUtils.getExpandedSearchingForPeersBinding(this)
    }

    private val collapsedSearchingForPeersInfoBinding:
            CollapsedSearchingForPeersInformationBinding by lazy {
        MainActivityDataBindingUtils.getCollapsedSearchingForPeersBinding(this)
    }

    private val connectedToPeerNoActionBottomSheetLayoutBinding:
            ConnectedToPeerNoActionPersistentBottomSheetLayoutBinding by lazy {
        MainActivityDataBindingUtils.getConnectedToPeerNoActionPersistentBottomSheetBinding(this)
    }

    private val connectedToPeerTransferOngoingBottomSheetLayoutBinding:
            ConnectedToPeerTransferOngoingPersistentBottomSheetBinding by lazy {
        MainActivityDataBindingUtils.getConnectedToPeerTransferOngoingPersistentBottomSheetBinding(
            this
        )
    }

    // persistent bottom sheet behavior variables
    private val searchingForPeersBottomSheetBehavior: BottomSheetBehavior<FrameLayout> by lazy {
        BottomSheetBehavior.from(
            activityMainBinding.connectionInfoPersistentBottomSheetLayout.root
        )
    }

    private val connectedToPeerNoActionBottomSheetBehavior: BottomSheetBehavior<FrameLayout> by lazy {
        BottomSheetBehavior.from(
            connectedToPeerNoActionBottomSheetLayoutBinding.root
        )
    }

    private val connectedToPeerTransferOngoingBottomSheetBehavior: BottomSheetBehavior<FrameLayout> by lazy {
        BottomSheetBehavior.from(
            connectedToPeerTransferOngoingBottomSheetLayoutBinding.root
        )
    }


    // service variables
    private var dataTransferService: DataTransferService? = null
    private val dataTransferServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service as DataTransferService.DataTransferServiceBinder
            dataTransferService = service.getServiceInstance()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            dataTransferService = null
        }
    }

    private val wifiDirectBroadcastReceiverCallback = object : WifiDirectBroadcastReceiverCallback {
        override fun wifiOn() {

        }

        override fun wifiOff() {

        }

        override fun peersListAvailable(peersList: MutableList<WifiP2pDevice>) {
            mainActivityViewModel.peersListAvailable(peersList)
        }

        override fun connectedToPeer(
            wifiP2pInfo: WifiP2pInfo,
            peeredDevice: WifiP2pDevice
        ) {
            startPeerDiscovery = false
            // update the ui to show that this device is connected to peer
            mainActivityViewModel.connectedToPeer(wifiP2pInfo, peeredDevice)
            if (dataTransferService?.isActive == true) {

            } else {
                // start data transfer service
                when (wifiP2pInfo.isGroupOwner) {
                    true -> {
                        // you are the server
                        Intent(
                            this@MainActivity,
                            DataTransferService::class.java
                        ).also { serviceIntent ->
                            serviceIntent.apply {
                                putExtra(DataTransferService.IS_SERVER, true)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(serviceIntent)
                            } else {
                                startService(serviceIntent)
                            }
                        }
                    }
                    false -> {
                        // you are the client
                        Intent(
                            this@MainActivity,
                            DataTransferService::class.java
                        ).also { serviceIntent ->
                            val serverIpAddress =
                                wifiP2pInfo.groupOwnerAddress.hostAddress
                            serviceIntent.apply {
                                putExtra(DataTransferService.IS_SERVER, false)
                                putExtra(DataTransferService.SERVER_IP_ADDRESS, serverIpAddress)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(serviceIntent)
                            } else {
                                startService(serviceIntent)
                            }
                        }
                    }
                }
            }
        }

        override fun wifiP2pDiscoveryStopped() {
            // in order to avoid disrupting the ui state due to multiple broadcast events
            // make sure you end the peer discovery only when the user specifies so
            if (shouldStopPeerDiscovery) {
                mainActivityViewModel.peerConnectionNoAction()
            } else {
                if (startPeerDiscovery) {
                    beginPeerDiscovery()
                }
            }
        }

        override fun wifiP2pDiscoveryStarted() {
            // only inform the view model that the device has began searching
            // for peers when there is no ui action
            if (mainActivityViewModel.peerConnectionUIState.value ==
                PeerConnectionUIState.NoConnectionUIAction
            ) {
                mainActivityViewModel.expandedSearchingForPeers()
            }
        }

        override fun disconnectedFromPeer() {
            mainActivityViewModel.peerConnectionNoAction()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate(layoutInflater).apply {
            activityMainBinding = this
            connectToPeerButton.setOnClickListener {
                if (it.alpha > 0f) {
                    configurePlatformOptionsModalBottomSheetLayout()
                    modalBottomSheetDialog.show()
                }
            }

            sendFileButton.setOnClickListener {
                /* TODO 1. Change the activity UI to show the list of elements in transfer
                2. As the progress of each elements happen, update the activity UI to reflect it
                * */
                mainActivityViewModel.addCurrentDataToTransferToUIState()
                mainActivityViewModel.expandedConnectedToPeerTransferOngoing()
                connectedToPeerTransferOngoingBottomSheetLayoutBinding
                    .expandedConnectedToPeerTransferOngoingLayout
                    .expandedConnectedToPeerTransferOngoingLayoutHeader
                    .apply {
                        ongoingTransferReceiveHeaderLayoutNoItemsInTransferTextView.root.animate()
                            .alpha(0f)
                        ongoingTransferReceiveHeaderLayoutDataTransferView.root.animate().alpha(1f)
                    }


                // transfer data using the DataTransferService
                dataTransferService?.transferData(
                    mainActivityViewModel.collectionOfDataToTransfer,
                ) { dataToTransfer: DataToTransfer,
                    percentTransferred: Float,
                    transferStatus: DataToTransfer.TransferStatus ->
                    when (transferStatus) {
                        DataToTransfer.TransferStatus.TRANSFER_STARTED -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                with(
                                    connectedToPeerTransferOngoingBottomSheetLayoutBinding
                                        .expandedConnectedToPeerTransferOngoingLayout
                                        .expandedConnectedToPeerTransferOngoingLayoutHeader
                                ) {
                                    ongoingTransferReceiveHeaderLayoutNoItemsInTransferTextView.root.animate()
                                        .alpha(0f)
                                    with(ongoingTransferReceiveHeaderLayoutDataTransferView) {
                                        dataSize =
                                            dataToTransfer.dataSize.transformDataSizeToMeasuredUnit()
                                        dataDisplayName = dataToTransfer.dataDisplayName
                                        Glide.with(ongoingDataTransferDataCategoryImageView)
                                            .load(dataToTransfer.dataUri)
                                            .into(ongoingDataTransferDataCategoryImageView)
                                    }
                                }
                            }
                        }
                        DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                            lifecycleScope.launch {
                                mainActivityViewModel.currentTransferHistory.find {
                                    it.id == dataToTransfer.dataUri.toString()
                                }.also {
                                    it?.let { ongoingDataTransferUIState ->
                                        val index =
                                            mainActivityViewModel.currentTransferHistory.indexOf(
                                                ongoingDataTransferUIState
                                            )
                                        ongoingDataTransferUIState as OngoingDataTransferUIState.DataItem
                                        ongoingDataTransferUIState.dataToTransfer.transferStatus =
                                            transferStatus
                                        withContext(Dispatchers.Main) {
                                            ongoingDataTransferRecyclerViewAdapter.submitList(
                                                mainActivityViewModel.currentTransferHistory
                                            )
                                            ongoingDataTransferRecyclerViewAdapter.notifyItemChanged(
                                                index
                                            )
                                        }
                                    }
                                }
                            }

                        }
                        DataToTransfer.TransferStatus.TRANSFER_ONGOING -> {
                            // update the transfer section of the UI
                            lifecycleScope.launch(Dispatchers.Main) {
                                with(
                                    connectedToPeerTransferOngoingBottomSheetLayoutBinding
                                        .expandedConnectedToPeerTransferOngoingLayout
                                        .expandedConnectedToPeerTransferOngoingLayoutHeader
                                ) {

                                    with(ongoingTransferReceiveHeaderLayoutDataTransferView) {
                                        dataTransferPercentAsString =
                                            "${percentTransferred.roundToInt()}%"
                                        dataTransferPercent = percentTransferred.roundToInt()
                                    }
                                }
                            }
                        }
                    }
                }
                mainActivityViewModel.clearCollectionOfDataToTransfer()
            }

            mainActivityAllMediaOnDevice.apply {
                // change the tab mode based on the current screen density
                allMediaOnDeviceTabLayout.tabMode = if (resources.configuration.fontScale > 1.1) {
                    TabLayout.MODE_SCROLLABLE
                } else TabLayout.MODE_FIXED

                allMediaOnDeviceViewPager.adapter = AllMediaOnDeviceViewPager2Adapter(
                    supportFragmentManager,
                    lifecycle
                )
                TabLayoutMediator(
                    allMediaOnDeviceTabLayout,
                    allMediaOnDeviceViewPager
                ) { tab, position ->
                    when (position) {
                        0 -> tab.text = "Apps"
                        1 -> tab.text = "Images"
                        2 -> tab.text = "Videos"
                        3 -> tab.text = "Music"
                        4 -> tab.text = "Files"
                    }
                }.attach()
            }
            setContentView(root)
        }
        lifecycle.apply {
            addObserver(ftsNotification)
        }
        observeViewModelLiveData()
        // bind to the data transfer service
        Intent(this, DataTransferService::class.java).also {
            bindService(it, dataTransferServiceConnection, BIND_AUTO_CREATE)
        }
    }

    private fun observeViewModelLiveData() {
        mainActivityViewModel.peerConnectionUIState.observe(this) {
            it?.let {
                when (it) {
                    is PeerConnectionUIState.CollapsedConnectedToPeerTransferOngoing -> {
                        if (!isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured) {
                            configureConnectedToPeerTransferOngoingBottomSheetLayout()
                        }
                        connectedToPeerTransferOngoingBottomSheetBehavior.apply {
                            state = BottomSheetBehavior.STATE_COLLAPSED
                            peekHeight = getBottomSheetPeekHeight()
                        }

                        // hide the connected to pair no action bottom sheet
                        connectedToPeerNoActionBottomSheetBehavior.apply {
                            isHideable = true
                            state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        connectedToPeerNoActionBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_HIDDEN
                    }
                    is PeerConnectionUIState.CollapsedSearchingForPeer -> {
                        // update the UI to display the number of devices found
                        if (!isSearchingForPeersBottomSheetLayoutConfigured) {
                            configureSearchingForPeersPersistentBottomSheetInfo()
                            expandedSearchingForPeersInfoBinding.root.alpha = 0f
                        }
                        collapsedSearchingForPeersInfoBinding.numberOfDevicesFound =
                            it.numberOfDevicesFound
                        collapseBottomSheet()
                    }
                    is PeerConnectionUIState.ExpandedConnectedToPeerTransferOngoing -> {
                        // Log.i("ReceivingInfo", "New file received UI update")
                        if (!isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured) {
                            configureConnectedToPeerTransferOngoingBottomSheetLayout()
                        }

                        // submit the list of items in transfer queue to the adapter
                        ongoingDataTransferRecyclerViewAdapter.submitList(it.collectionOfDataToTransfer)
                        ongoingDataTransferRecyclerViewAdapter.notifyDataSetChanged()


                        connectedToPeerTransferOngoingBottomSheetBehavior.apply {
                            state =
                                BottomSheetBehavior.STATE_EXPANDED
                            peekHeight =
                                getBottomSheetPeekHeight()
                        }
                        // hide the connected to pair no action bottom sheet
                        connectedToPeerNoActionBottomSheetBehavior.apply {
                            isHideable = true
                            state = BottomSheetBehavior.STATE_HIDDEN
                        }
                    }
                    is PeerConnectionUIState.ExpandedSearchingForPeer -> {
                        if (!isSearchingForPeersBottomSheetLayoutConfigured) {
                            configureSearchingForPeersPersistentBottomSheetInfo()
                            collapsedSearchingForPeersInfoBinding.root.alpha = 0f
                        }
                        discoveredPeersRecyclerViewAdapter.submitList(it.devices.map { wifiP2pDevice ->
                            DiscoveredPeersDataItem.DiscoveredPeer(wifiP2pDevice)
                        }.toMutableList())
                        searchingForPeersBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_EXPANDED
                        searchingForPeersBottomSheetBehavior.peekHeight = getBottomSheetPeekHeight()
                    }
                    PeerConnectionUIState.NoConnectionUIAction -> {
                        if (isConnectedToPeerNoActionBottomSheetLayoutConfigured) {
                            connectedToPeerNoActionBottomSheetBehavior.isHideable = true
                            connectedToPeerNoActionBottomSheetBehavior.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            isConnectedToPeerNoActionBottomSheetLayoutConfigured = false
                        }
                        if (isSearchingForPeersBottomSheetLayoutConfigured) {
                            searchingForPeersBottomSheetBehavior.isHideable = true
                            searchingForPeersBottomSheetBehavior.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            isSearchingForPeersBottomSheetLayoutConfigured = false
                        }
                    }
                    is PeerConnectionUIState.CollapsedConnectedToPeerNoAction -> {
                        // in case of a configuration or theme change, inflate and configure the bottom sheet
                        if (!isConnectedToPeerNoActionBottomSheetLayoutConfigured) {
                            configureConnectedToPeerNoActionBottomSheetLayoutInfo(
                                it.connectedDevice
                            )
                        }
                        // hide the searching for peers bottom
                        searchingForPeersBottomSheetBehavior.isHideable = true
                        searchingForPeersBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_HIDDEN

                        // show the send button
                        activityMainBinding.sendFileButton.animate().alpha(1f)

                        // stop searching for peers animation
                        expandedSearchingForPeersInfoBinding
                            .expandedSearchingForPeersInformationSearchingForDevicesAnimation
                            .setKeepAnimating(false)
                        collapsedSearchingForPeersInfoBinding
                            .mediumSearchingForPeersAnimation
                            .setKeepAnimating(false)

                        // hide the expanded connected to pair no action layout
                        connectedToPeerNoActionBottomSheetLayoutBinding
                            .expandedConnectedToPeerNoActionLayout
                            .root
                            .alpha = 0f

                        // set bottom sheet peek height
                        connectedToPeerNoActionBottomSheetBehavior.peekHeight =
                            getBottomSheetPeekHeight()
                        connectedToPeerNoActionBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_COLLAPSED
                    }

                    is PeerConnectionUIState.ExpandedConnectedToPeerNoAction -> {
                        if (!isConnectedToPeerNoActionBottomSheetLayoutConfigured) configureConnectedToPeerNoActionBottomSheetLayoutInfo(
                            it.connectedDevice
                        )
                        // hide the searching for peers bottom
                        searchingForPeersBottomSheetBehavior.isHideable = true
                        searchingForPeersBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_HIDDEN

                        connectedToPeerNoActionBottomSheetLayoutBinding
                            .collapsedConnectedToPeerNoActionLayout
                            .root
                            .alpha = 0f
                        connectedToPeerNoActionBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }
    }

    private fun configureConnectedToPeerTransferOngoingBottomSheetLayout() {
        isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured = true
        connectedToPeerTransferOngoingBottomSheetLayoutBinding.apply {
            collapsedConnectedToPeerOngoingDataTransferLayout.apply {

            }
            expandedConnectedToPeerTransferOngoingLayout.apply {
                expandedConnectedToPeerTransferOngoingRecyclerView.apply {
                    adapter = ongoingDataTransferRecyclerViewAdapter
                    val gridLayoutManager = GridLayoutManager(this@MainActivity, 3)
                    gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (ongoingDataTransferRecyclerViewAdapter.getItemViewType(
                                position
                            )) {
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_WAITING.value -> 1
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_OR_RECEIVE_COMPLETE.value -> 1
                                OngoingDataTransferAdapterViewTypes.CATEGORY_HEADER.value -> 3
                                else -> 3
                            }
                        }
                    }
                    layoutManager = gridLayoutManager
                    setHasFixedSize(true)
                }
                expandedConnectedToPeerTransferOngoingLayoutHeader.apply {
                    ongoingTransferReceiveHeaderLayoutDataTransferView.root.animate().alpha(0f)
                    ongoingTransferReceiveHeaderLayoutDataReceiveView.root.animate().alpha(0f)
                }
            }
        }
        connectedToPeerTransferOngoingBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mainActivityViewModel.expandedConnectedToPeerTransferOngoing()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mainActivityViewModel.collapsedConnectedToPeerTransferOngoing()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                connectedToPeerTransferOngoingBottomSheetLayoutBinding
                    .collapsedConnectedToPeerOngoingDataTransferLayout
                    .root.alpha = 1 - slideOffset * 3.5f
                connectedToPeerTransferOngoingBottomSheetLayoutBinding
                    .expandedConnectedToPeerTransferOngoingLayout
                    .root.alpha = slideOffset
            }
        })
    }

    private fun configureConnectedToPeerNoActionBottomSheetLayoutInfo(connectedDevice: WifiP2pDevice) {
        isConnectedToPeerNoActionBottomSheetLayoutConfigured = true
        connectedToPeerNoActionBottomSheetLayoutBinding.apply {
            collapsedConnectedToPeerNoActionLayout.apply {
                deviceConnectedTo = "Connected to ${connectedDevice.deviceName ?: "unknown device"}"
                collapsedConnectedToPeerNoTransferBreakConnectionButton.setOnClickListener {

                }
                collapsedConnectedToPeerNoTransferBreakConnectionButton.setOnClickListener {
                    cancelDeviceConnection()
                    // TODO, remove later
                    mainActivityViewModel.peerConnectionNoAction()
                }

                root.setOnClickListener {
                    mainActivityViewModel.expandedConnectedToPeerNoAction()
                }
            }
            expandedConnectedToPeerNoActionLayout.apply {
                deviceAddress = connectedDevice.deviceAddress
                deviceName = connectedDevice.deviceName
                collapseExpandedConnectedToPeerNoActionImageButton.setOnClickListener {
                    mainActivityViewModel.collapsedConnectedToPeerNoAction()
                }
                expandedConnectedToPeerNoActionCloseConnectionImageButton.setOnClickListener {
                    cancelDeviceConnection()
                    // TODO, remove later
                    mainActivityViewModel.peerConnectionNoAction()
                }
            }
        }
        connectedToPeerNoActionBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mainActivityViewModel.expandedConnectedToPeerNoAction()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mainActivityViewModel.collapsedConnectedToPeerNoAction()
                    }
                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                connectedToPeerNoActionBottomSheetLayoutBinding.collapsedConnectedToPeerNoActionLayout.root.alpha =
                    1 - slideOffset * 3.5f
                connectedToPeerNoActionBottomSheetLayoutBinding
                    .expandedConnectedToPeerNoActionLayout.root.alpha = slideOffset
            }
        })
    }


    private fun configureSearchingForPeersPersistentBottomSheetInfo() {
        isSearchingForPeersBottomSheetLayoutConfigured = true

        collapsedSearchingForPeersInfoBinding.apply {
            collapsedSearchingForPeersInformationCancelSearchingForPeers.setOnClickListener {
                stopDevicePeerDiscovery()
            }
            root.setOnClickListener {
                mainActivityViewModel.expandedSearchingForPeers()
            }
        }

        expandedSearchingForPeersInfoBinding.apply {
            collapseExpandedSearchingForPeersImageButton.setOnClickListener {
                mainActivityViewModel.collapsedSearchingForPeers()
            }
            expandedSearchingForPeersInformationStopSearchButton.setOnClickListener {
                stopDevicePeerDiscovery()
            }
            expandedSearchingForPeersInformationDiscoveredPeersRecyclerView.apply {
                adapter = discoveredPeersRecyclerViewAdapter
            }
        }
        searchingForPeersBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mainActivityViewModel.collapsedSearchingForPeers()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mainActivityViewModel.expandedSearchingForPeers()
                    }
                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                collapsedSearchingForPeersInfoBinding.root.alpha = 1 - slideOffset * 3.5f
                expandedSearchingForPeersInfoBinding.root.alpha = slideOffset
            }
        })
    }

    private fun collapseBottomSheet() {
        searchingForPeersBottomSheetBehavior.peekHeight = getBottomSheetPeekHeight()
        searchingForPeersBottomSheetBehavior.state =
            BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun configurePlatformOptionsModalBottomSheetLayout() {
        modalBottomSheetDialog = BottomSheetDialog(this@MainActivity)
        val modalBottomSheetLayoutBinding =
            ZipBoltConnectionOptionsBottomSheetLayoutBinding.inflate(layoutInflater)

        modalBottomSheetLayoutBinding.apply {
            connectToAndroid.setOnClickListener {
                if (!isSearchingForPeersBottomSheetLayoutConfigured) configureSearchingForPeersPersistentBottomSheetInfo()
                activityMainBinding.apply {
                    connectionInfoPersistentBottomSheetLayout.apply {
                        modalBottomSheetDialog.dismiss()
                        beginPeerDiscovery()
                        /*    connectToPeerButton.animate().alpha(0f).start()
                            connectionInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            connectionInfoBottomSheetBehavior.peekHeight = getBottomSheetPeekHeight()*/
                    }
                }
            }
            connectToIphone.setOnClickListener {
                displayToast("Connect to iPhone")
            }
            connectToDesktop.setOnClickListener {
                displayToast("Connect to Desktop")
            }
            modalBottomSheetDialog.setContentView(root)
        }
    }

    fun addToDataToTransferList(dataToTransfer: DataToTransfer) {
        mainActivityViewModel.addDataToTransfer(dataToTransfer)
        //  displayToast("Clicked ${mainActivityViewModel.collectionOfDataToTransfer.size}")
    }

    fun removeFromDataToTransferList(dataToTransfer: DataToTransfer) {
        mainActivityViewModel.removeDataFromDataToTransfer(dataToTransfer)
        //displayToast("Clicked ${mainActivityViewModel.collectionOfDataToTransfer.size}")
    }


    private fun getBottomSheetPeekHeight(): Int {
        return (60 * resources.displayMetrics.density).roundToInt()
    }


    @SuppressLint("MissingPermission", "HardwareIds")
    private fun createWifiDirectGroup() {
        val wifiP2pConfig = WifiP2pConfig().apply {
            deviceAddress = wifiManager.connectionInfo.macAddress
            wps.setup = WpsInfo.PBC
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            wifiP2pManager.createGroup(wifiP2pChannel, wifiP2pConfig,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        displayToast("Group created successfully")
                    }

                    override fun onFailure(p0: Int) {
                        displayToast("Group creation failed")
                    }
                })
        } else {
            wifiP2pManager.createGroup(wifiP2pChannel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        displayToast("Group created successfully")
                        wifiP2pManager.requestGroupInfo(wifiP2pChannel) {
                            it?.let {
                                Toast.makeText(
                                    this@MainActivity, "Password is " +
                                            it.passphrase, Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(p0: Int) {
                        displayToast("Group creation failed")
                    }
                })
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToADevice(device: WifiP2pDevice) {
        val wifiP2pConfiguration = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        wifiP2pManager.connect(wifiP2pChannel, wifiP2pConfiguration,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Broadcast receiver notifies us in WIFI_P2P_CONNECTION_CHANGED_ACTION
                    displayToast("Connection attempt successful")
                }

                override fun onFailure(p0: Int) {
                    // connection initiation failed,
                    displayToast("Connection attempt failed")
                }
            })
    }


    @SuppressLint("MissingPermission")
    private fun beginPeerDiscovery() {
        if (isLocationPermissionGranted()) {
            wifiP2pManager.discoverPeers(wifiP2pChannel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    startPeerDiscovery = true
                    // TODO Peer discovery started alert the user
                    //  displayToast("Peer discovery successfully initiated")

                }

                override fun onFailure(p0: Int) {
                    // TODO Peer discovery initiation failed, alert the user
                    displayToast("Peer discovery initiation failed")
                }

            })
        } else {
            checkFineLocationPermission()
        }
    }


    private fun cancelDeviceConnection() {
        wifiP2pManager.removeGroup(wifiP2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    displayToast("P2p connection canceled")
                }

                override fun onFailure(reason: Int) {
                    displayToast("Cannot disconnect from device")
                }
            })
    }

    private fun stopDevicePeerDiscovery() {
        if (isLocationPermissionGranted()) {
            wifiP2pManager.stopPeerDiscovery(
                wifiP2pChannel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        shouldStopPeerDiscovery = true
                    }

                    override fun onFailure(p0: Int) {
                        displayToast("Couldn't stop peer discovery")
                    }
                })
        }
    }

    private fun createSystemBroadcastIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        }
    }

    private fun createLocalBroadcastIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(IncomingDataBroadcastReceiver.INCOMING_DATA_BYTES_RECEIVED_ACTION)
        }
    }

    private fun initializeChannelAndBroadcastReceiver() {
        wifiP2pChannel =
            wifiP2pManager.initialize(
                this, mainLooper
            )
            // The channel to the framework has been disconnected.
            // Application could try re-initializing
            {

            }
        // use the activity, wifiP2pManager and wifiP2pChannel to initialize the wifiDiectBroadcastReceiver
        wifiP2pChannel.also { channel: WifiP2pManager.Channel ->
            wifiDirectBroadcastReceiver = WifiDirectBroadcastReceiver(
                wifiDirectBroadcastReceiverCallback = wifiDirectBroadcastReceiverCallback,
                connectivityManager = connectivityManager,
                wifiP2pManager = wifiP2pManager,
                wifiP2pChannel = wifiP2pChannel
            )
        }
    }

    // check if SpeedForce has access to device fine location
    private fun checkFineLocationPermission() {
        val isFineLocationPermissionGranted = isLocationPermissionGranted()

        if (isFineLocationPermissionGranted) {
            // TODO check if the device location is on, using location manager
            //TODO more resource @ https://developer.android.com/training/location
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_REQUEST_CODE
            )
        }
    }

    private fun isLocationPermissionGranted() = ActivityCompat.checkSelfPermission(
        this,
        permission.ACCESS_FINE_LOCATION
    ) ==
            PackageManager.PERMISSION_GRANTED

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        PermissionUtils.checkReadAndWriteExternalStoragePermission(this)
        // register the broadcast receiver
        initializeChannelAndBroadcastReceiver()
        registerReceiver(wifiDirectBroadcastReceiver, createSystemBroadcastIntentFilter())
        localBroadcastManager.registerReceiver(
            incomingDataBroadcastReceiver,
            createLocalBroadcastIntentFilter()
        )
        Intent(this, DataTransferService::class.java).also {
            bindService(it, dataTransferServiceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(dataTransferServiceConnection)
        // unregister the broadcast receiver
        unregisterReceiver(wifiDirectBroadcastReceiver)
        localBroadcastManager.unregisterReceiver(incomingDataBroadcastReceiver)
    }

    override fun onBackPressed() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE && permissions.contains(permission.ACCESS_FINE_LOCATION)) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // access to device fine location has been granted to SpeedForce
                    // TODO check if the device location is on, using location manager
                    //TODO more resource @ https://developer.android.com/training/location
                }
            }
        } else if (requestCode == PermissionUtils.READ_WRITE_STORAGE_REQUEST_CODE && permissions.contains(
                permission.READ_EXTERNAL_STORAGE
            ) &&
            permissions.contains(permission.WRITE_EXTERNAL_STORAGE)
        ) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // SpeedForce has permission to read and write ot the device external storage
                    // TODO Alert the viewModel to go ahead and fetch media and files from the repositories
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}


