package com.salesground.zipbolt


import android.Manifest.*
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.salesground.zipbolt.broadcast.IncomingDataBroadcastReceiver
import com.salesground.zipbolt.broadcast.WifiDirectBroadcastReceiver
import com.salesground.zipbolt.broadcast.WifiDirectBroadcastReceiver.WifiDirectBroadcastReceiverCallback
import com.salesground.zipbolt.databinding.*
import com.salesground.zipbolt.databinding.ActivityMainBinding.inflate
import com.salesground.zipbolt.model.ui.DiscoveredPeersDataItem
import com.salesground.zipbolt.model.ui.PeerConnectionUIState

import com.salesground.zipbolt.notification.FileTransferServiceNotification
import com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation.DiscoveredPeersRecyclerViewAdapter
import com.salesground.zipbolt.ui.AllMediaOnDeviceViewPager2Adapter
import com.salesground.zipbolt.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
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

    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var wifiDirectBroadcastReceiver: WifiDirectBroadcastReceiver

    private val localBroadCastReceiver: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }
    private val incomingDataBroadcastReceiver: IncomingDataBroadcastReceiver by lazy {
        IncomingDataBroadcastReceiver()
    }
    private val wifiDirectBroadcastReceiverCallback = object : WifiDirectBroadcastReceiverCallback {
        override fun wifiOn() {

        }

        override fun wifiOff() {

        }

        override fun peersListAvailable(peersList: MutableList<WifiP2pDevice>) {
            mainActivityViewModel.peersListAvailable(peersList)
        }

        override fun peeredDeviceConnectionInfoReady(wifiP2pInfo: WifiP2pInfo) {
            mainActivityViewModel.connectedToPeer(wifiP2pInfo)
        }

        override fun wifiP2pDiscoveryStopped() {
            mainActivityViewModel.wifiP2pDiscoveryStopped()
        }

        override fun wifiP2pDiscoveryStarted() {
            mainActivityViewModel.wifiP2pDiscoveryStarted()
        }
    }

    // ui variables

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var modalBottomSheetDialog: BottomSheetDialog
    private val connectionInfoBottomSheetBehavior: BottomSheetBehavior<FrameLayout> by lazy {
        BottomSheetBehavior.from(
            activityMainBinding.connectionInfoPersistentBottomSheetLayout.root
        )
    }
    private var isBottomSheetLayoutConfigured: Boolean = false
    private val discoveredPeersRecyclerViewAdapter: DiscoveredPeersRecyclerViewAdapter by lazy {
        DiscoveredPeersRecyclerViewAdapter(
            connectToDeviceClickListener = object :
                DiscoveredPeersRecyclerViewAdapter.ConnectToDeviceClickListener {
                override fun onConnectToDevice(wifiP2pDevice: WifiP2pDevice) {
                    connectToADevice(wifiP2pDevice)
                    // TODO
                    /**
                     * 1. Collapse the searching for peers expanded bottom sheet ui
                     * 2. Display the connected to peer collapsed bottom sheet ui
                     * 3. Stop peer discovery
                     * **/
                }
            }
        )
    }

    private val expandedSearchingForPeersInfoBinding:
            ExpandedSearchingForPeersInformationBinding by lazy {
        DataBindingUtils.getExpandedSearchingForPeersBinding(this)
    }

    private val collapsedSearchingForPeersInfoBinding:
            CollapsedSearchingForPeersInformationBinding by lazy {
        DataBindingUtils.getCollapsedSearchingForPeersBinding(this)
    }

    private val expandedConnectedToPeerNoActionBinding:
            ExpandedConnectedToPeerNoActionBinding by lazy {
        DataBindingUtils.getExpandedConnectedToPeerNoActionBinding(this)
    }

    private val collapsedConnectedToPeerNoActionBinding:
            CollapsedConnectedToPeerNoActionBinding by lazy {
        DataBindingUtils.getCollapsedConnectedToPeerNoActionBinding(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate(layoutInflater).apply {
            setContentView(root)
            activityMainBinding = this
            connectToPeerButton.setOnClickListener {
                if (it.alpha > 0f) {
                    configurePlatformOptionsModalBottomSheetLayout()
                    modalBottomSheetDialog.show()
                }
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
        }
        isBottomSheetLayoutConfigured = false
    }

    private fun observeViewModelLiveData() {
        mainActivityViewModel.peerConnectionUIState.observe(this) {
            it?.let {
                when (it) {
                    is PeerConnectionUIState.CollapsedConnectedToPeer -> {
                        if (!isBottomSheetLayoutConfigured) configureSearchingForPeersPersistentBottomSheetInfo()

                    }
                    is PeerConnectionUIState.CollapsedSearchingForPeer -> {
                        // update the UI to display the number of devices found
                        if (!isBottomSheetLayoutConfigured) {
                            configureSearchingForPeersPersistentBottomSheetInfo()
                            expandedSearchingForPeersInfoBinding.root.alpha = 0f
                        }
                        collapsedSearchingForPeersInfoBinding.numberOfDevicesFound =
                            it.numberOfDevicesFound
                        collapseBottomSheet()
                    }
                    is PeerConnectionUIState.ExpandedConnectedToPeer -> {
                        if (!isBottomSheetLayoutConfigured) configureSearchingForPeersPersistentBottomSheetInfo()
                    }
                    is PeerConnectionUIState.ExpandedSearchingForPeer -> {
                        if (!isBottomSheetLayoutConfigured) {
                            configureSearchingForPeersPersistentBottomSheetInfo()
                            collapsedSearchingForPeersInfoBinding.root.alpha = 0f
                        }
                        discoveredPeersRecyclerViewAdapter.submitList(it.devices.map { wifiP2pDevice ->
                            DiscoveredPeersDataItem.DiscoveredPeer(wifiP2pDevice)
                        }.toMutableList())
                        connectionInfoBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_EXPANDED
                        connectionInfoBottomSheetBehavior.peekHeight = getBottomSheetPeekHeight()
                    }
                    PeerConnectionUIState.NoConnectionUIAction -> {
                        connectionInfoBottomSheetBehavior.isHideable = true
                        connectionInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    is PeerConnectionUIState.CollapsedConnectedToPeerNoAction -> {
                        if (!isBottomSheetLayoutConfigured) configureSearchingForPeersPersistentBottomSheetInfo()
                        collapseBottomSheet()
                    }
                    is PeerConnectionUIState.ExpandedConnectedToPeerNoAction -> {
                        if (!isBottomSheetLayoutConfigured) configureSearchingForPeersPersistentBottomSheetInfo()
                    }
                }
            }
        }
    }

    private fun configurePersistentInfoBottomSheetCallback() {
        connectionInfoBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        when (mainActivityViewModel.peerConnectionUIState.value) {
                            is PeerConnectionUIState.ExpandedConnectedToPeer -> {

                            }
                            is PeerConnectionUIState.ExpandedConnectedToPeerNoAction -> {

                            }
                            is PeerConnectionUIState.ExpandedSearchingForPeer -> {
                                mainActivityViewModel.collapsedSearchingForPeers()
                            }
                            PeerConnectionUIState.NoConnectionUIAction -> {

                            }
                            null -> {
                            }
                        }
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        when (mainActivityViewModel.peerConnectionUIState.value) {
                            is PeerConnectionUIState.CollapsedConnectedToPeer -> {
                            }
                            is PeerConnectionUIState.CollapsedConnectedToPeerNoAction -> {

                            }
                            is PeerConnectionUIState.CollapsedSearchingForPeer -> {
                                mainActivityViewModel.expandedSearchingForPeers()
                            }
                            PeerConnectionUIState.NoConnectionUIAction -> {

                            }
                            null -> TODO()
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                when (mainActivityViewModel.peerConnectionUIState.value) {
                    is PeerConnectionUIState.CollapsedConnectedToPeer -> {

                    }
                    is PeerConnectionUIState.CollapsedConnectedToPeerNoAction -> {
                    }
                    is PeerConnectionUIState.CollapsedSearchingForPeer -> {
                        collapsedSearchingForPeersInfoBinding.root.alpha = 1 - slideOffset * 3.5f
                        expandedSearchingForPeersInfoBinding.root.alpha = slideOffset
                    }
                    is PeerConnectionUIState.ExpandedConnectedToPeer -> {
                    }
                    is PeerConnectionUIState.ExpandedConnectedToPeerNoAction -> {
                    }
                    is PeerConnectionUIState.ExpandedSearchingForPeer -> {
                        collapsedSearchingForPeersInfoBinding.root.alpha = 1 - slideOffset * 3.5f
                        expandedSearchingForPeersInfoBinding.root.alpha = slideOffset
                    }
                    PeerConnectionUIState.NoConnectionUIAction -> {
                    }
                    null -> {
                    }
                }
            }
        })
    }

    private fun configureConnectedToPeerNoActionPersistentBottomSheetInfo(){
        collapsedConnectedToPeerNoActionBinding.apply{

        }
    }
    private fun configureSearchingForPeersPersistentBottomSheetInfo() {
        isBottomSheetLayoutConfigured = true

        collapsedSearchingForPeersInfoBinding.apply {
            collapsedSearchingForPeersInformationCancelSearchingForPeers.setOnClickListener {
                stopDevicePeerDiscovery()
            }
            root.setOnClickListener {
                mainActivityViewModel.expandedSearchingForPeers()
                // connectionInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        expandedSearchingForPeersInfoBinding.apply {
            collapseExpandedSearchingForPeersImageButton.setOnClickListener {
                mainActivityViewModel.collapsedSearchingForPeers()
                // connectionInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            expandedSearchingForPeersInformationStopSearchButton.setOnClickListener {
                stopDevicePeerDiscovery()
            }
            expandedSearchingForPeersInformationDiscoveredPeersRecyclerView.apply {
                adapter = discoveredPeersRecyclerViewAdapter
            }
        }
        configurePersistentInfoBottomSheetCallback()
    }

    private fun collapseBottomSheet() {
        connectionInfoBottomSheetBehavior.peekHeight = getBottomSheetPeekHeight()
        connectionInfoBottomSheetBehavior.state =
            BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun configurePlatformOptionsModalBottomSheetLayout() {
        modalBottomSheetDialog = BottomSheetDialog(this@MainActivity)
        val modalBottomSheetLayoutBinding =
            ZipBoltConnectionOptionsBottomSheetLayoutBinding.inflate(layoutInflater)

        modalBottomSheetLayoutBinding.apply {
            connectToAndroid.setOnClickListener {
                if (!isBottomSheetLayoutConfigured) configureSearchingForPeersPersistentBottomSheetInfo()
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

    private fun getBottomSheetPeekHeight(): Int {
        return (60 * resources.displayMetrics.density).roundToInt()
    }

    private fun createNotificationChannel() {
        ftsNotification.createFTSNotificationChannel()
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


    private fun stopDevicePeerDiscovery() {
        if (isLocationPermissionGranted()) {
            wifiP2pManager.stopPeerDiscovery(
                wifiP2pChannel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        /*mainActivityViewModel.updatePeerConnectionState(peerConnectionState =
                        PeerConnectionUIState.NoConnectionUIAction)*/
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
        // register the broadcast receiver
        initializeChannelAndBroadcastReceiver()

        observeViewModelLiveData()
        createNotificationChannel()
        //checkReadAndWriteExternalStoragePermission()
        PermissionUtils.checkReadAndWriteExternalStoragePermission(this)
        registerReceiver(wifiDirectBroadcastReceiver, createSystemBroadcastIntentFilter())
        localBroadCastReceiver.registerReceiver(
            incomingDataBroadcastReceiver,
            createLocalBroadcastIntentFilter()
        )
    }

    override fun onStop() {
        super.onStop()
        // unregister the broadcast receiver
        unregisterReceiver(wifiDirectBroadcastReceiver)
        localBroadCastReceiver.unregisterReceiver(incomingDataBroadcastReceiver)
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
        } else if (requestCode == READ_WRITE_STORAGE_REQUEST_CODE && permissions.contains(permission.READ_EXTERNAL_STORAGE) &&
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


