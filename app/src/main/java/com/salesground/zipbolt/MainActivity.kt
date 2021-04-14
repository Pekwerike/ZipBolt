package com.salesground.zipbolt


import android.Manifest.*
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.salesground.zipbolt.broadcast.WifiDirectBroadcastReceiver
import com.salesground.zipbolt.databinding.*
import com.salesground.zipbolt.databinding.ActivityMainBinding.inflate

import com.salesground.zipbolt.foregroundservice.ClientService
import com.salesground.zipbolt.foregroundservice.ServerService
import com.salesground.zipbolt.model.ui.PeerConnectionState
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation.DiscoveredPeersDataItem
import com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation.DiscoveredPeersRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.allmediadisplay.AllMediaOnDeviceViewPager2Adapter
import com.salesground.zipbolt.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt


private const val FINE_LOCATION_REQUEST_CODE = 100
private const val READ_WRITE_STORAGE_REQUEST_CODE = 101

const val OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE = 1010
const val SERVER_IP_ADDRESS_KEY = "ServerIpAddress"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel by viewModels<MainActivityViewModel>()
    // private val mediaViewModel by viewModels<MediaViewModel>()
    // private val deviceMediaViewModel by viewModels<ImagesViewModel>()
    // private val homeScreenViewModel by viewModels<HomeScreenViewModel>()

    private val wifiP2pManager: WifiP2pManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }

    @Inject
    lateinit var ftsNotification: FileTransferServiceNotification


    //  private val deviceApplicationViewModel: DeviceApplicationViewModel by viewModels()

    private val wifiManager: WifiManager by lazy(LazyThreadSafetyMode.NONE) {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var wifiDirectBroadcastReceiver: WifiDirectBroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    private var isServerServiceBound: Boolean = false
    private var isClientServiceBound: Boolean = false

    // ui variables
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var modalBottomSheetDialog: BottomSheetDialog
    private lateinit var connectionInfoBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var isBottomSheetLayoutConfigured: Boolean = false
    private val discoveredPeersRecyclerViewAdapter: DiscoveredPeersRecyclerViewAdapter by lazy {
        DiscoveredPeersRecyclerViewAdapter(
            connectToDeviceClickListener = object :
                DiscoveredPeersRecyclerViewAdapter.ConnectToDeviceClickListener {
                override fun onConnectToDevice(wifiP2pDevice: WifiP2pDevice) {

                }
            }
        )
    }


    private val expandedSearchingForPeersInfoBinding:
            ExpandedSearchingForPeersInformationBinding by lazy {
        val expandedSearchingForPeersInfoView =
            findViewById<ViewStub>(R.id.expanded_searching_for_peers_info_view_stub).inflate()
        DataBindingUtil.bind<ExpandedSearchingForPeersInformationBinding>(
            expandedSearchingForPeersInfoView
        )
        DataBindingUtil.getBinding(expandedSearchingForPeersInfoView)!!
    }

    private val collapsedSearchingForPeersInfoBinding:
            CollapsedSearchingForPeersInformationBinding by lazy {
        val collapsedSearchingForPeerView =
            findViewById<ViewStub>(R.id.collapsed_searching_for_peers_info_view_stub).inflate()
        DataBindingUtil.bind<CollapsedSearchingForPeersInformationBinding>(
            collapsedSearchingForPeerView
        )
        DataBindingUtil.getBinding(collapsedSearchingForPeerView)!!
    }


    private val clientServiceConnection: ServiceConnection by lazy {
        object : ServiceConnection {

            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

                val clientServiceBinder = p1 as ClientService.ClientServiceBinder
                mainActivityViewModel.clientServiceReady(clientServiceBinder.getClientServiceBinder())
                isClientServiceBound = true
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                isClientServiceBound = false
            }
        }
    }

    private val serverServiceConnection: ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                val serverServiceBinder = p1 as ServerService.ServerServiceBinder
                mainActivityViewModel.serverServiceReady(serverServiceBinder.getServerServiceInstance())
                isServerServiceBound = true
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                isServerServiceBound = false
            }
        }
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
    }

    private fun configureConnectionInfoPersistentBottomSheet() {
        isBottomSheetLayoutConfigured = true
        connectionInfoBottomSheetBehavior =
            BottomSheetBehavior.from(
                activityMainBinding.connectionInfoPersistentBottomSheetLayout.root
            )

        collapsedSearchingForPeersInfoBinding.apply {
            collapsedSearchingForPeersInformationCancelSearchingForPeers.setOnClickListener {
                stopDevicePeerDiscovery()
            }
            root.setOnClickListener {
                connectionInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        connectionInfoBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mainActivityViewModel.updatePeerConnectionState(
                            peerConnectionState =
                            PeerConnectionState.CollapsedSearchingForPeer(
                                numberOfDevicesFound =
                                0
                            )
                        )
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mainActivityViewModel.updatePeerConnectionState(
                            peerConnectionState = PeerConnectionState.ExpandedSearchingForPeer(
                                devices = mutableListOf()
                            )
                        )
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                collapsedSearchingForPeersInfoBinding.root.alpha = 1 - slideOffset * 3.5f
                expandedSearchingForPeersInfoBinding.root.alpha = slideOffset
            }
        })
        expandedSearchingForPeersInfoBinding.apply {
            collapseExpandedSearchingForPeersImageButton.setOnClickListener {
                connectionInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            expandedSearchingForPeersInformationStopSearchButton.setOnClickListener {
                stopDevicePeerDiscovery()
            }
            expandedSearchingForPeersInformationDiscoveredPeersRecyclerView.apply {
                adapter = discoveredPeersRecyclerViewAdapter
            }
        }
    }

    private fun configurePlatformOptionsModalBottomSheetLayout() {
        modalBottomSheetDialog = BottomSheetDialog(this@MainActivity)
        val modalBottomSheetLayoutBinding =
            ZipBoltConnectionOptionsBottomSheetLayoutBinding.inflate(layoutInflater)

        modalBottomSheetLayoutBinding.apply {
            connectToAndroid.setOnClickListener {
                if (!isBottomSheetLayoutConfigured) configureConnectionInfoPersistentBottomSheet()
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

    /*   private fun imageSelected(image: MediaModel) {
           mediaViewModel.imageSelected(image)
           displayToast(image.mimeType)
       }

       private fun transferImages() {
           val selectedImages = mediaViewModel.selectedImagesForTransfer.value
           if (mainActivityViewModel.clientService.value != null) {
               selectedImages?.let {
                   mainActivityViewModel.clientService.value?.transferMediaItems(selectedImages)
                   displayToast("transfering")
               }
           } else if (mainActivityViewModel.serverService.value != null) {
               selectedImages?.let {
                   mainActivityViewModel.serverService.value?.transferMediaItems(selectedImages)
                   displayToast("transfering")
               }
           }
       }*/

    private fun createNotificationChannel() {
        ftsNotification.createFTSNotificationChannel()
    }

    fun peeredDeviceConnectionInfoReady(deviceConnectionInfo: WifiP2pInfo) {
        mainActivityViewModel.peeredDeviceConnectionInfoUpdated(connectionInfo = deviceConnectionInfo)
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
                }

                override fun onFailure(p0: Int) {
                    // connection initiation failed,
                    // TODO Alert user of failed connection attempt
                }

            })
    }

    fun peersListAvailable(peersList: MutableList<WifiP2pDevice>) {
        mainActivityViewModel.discoveredPeersListChanged(peersList)
    }

    @SuppressLint("MissingPermission")
    private fun beginPeerDiscovery() {
        if (isLocationPermissionGranted()) {
            wifiP2pManager.discoverPeers(wifiP2pChannel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // TODO Peer discovery started alert the user
                    //  displayToast("Peer discovery successfully initiated")
                    mainActivityViewModel.updatePeerConnectionState(
                        peerConnectionState = PeerConnectionState.ExpandedSearchingForPeer(
                            devices = mutableListOf()
                        )
                    )
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
                        PeerConnectionState.NoConnectionAction)*/
                    }

                    override fun onFailure(p0: Int) {
                        displayToast("Couldn't stop peer discovery")
                    }

                })
        }
    }


    private fun registerIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        }
    }

    fun wifiP2pDiscoveryStopped() {
        mainActivityViewModel.updatePeerConnectionState(
            peerConnectionState =
            PeerConnectionState.NoConnectionAction
        )
    }

    private fun observeViewModelLiveData() {
        mainActivityViewModel.peerConnectionState.observe(this) {
            it?.let { peerConnectionState ->
                when (peerConnectionState) {
                    is PeerConnectionState.CollapsedConnectedToPeer -> {

                    }
                    is PeerConnectionState.CollapsedSearchingForPeer -> {
                        if (!isBottomSheetLayoutConfigured) configureConnectionInfoPersistentBottomSheet()
                        connectionInfoBottomSheetBehavior.isHideable = false
                        connectionInfoBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_COLLAPSED
                        expandedSearchingForPeersInfoBinding.root.alpha = 0f
                        activityMainBinding.connectToPeerButton.alpha = 0f
                        activityMainBinding.sendFileButton.animate().alpha(1f).start()
                        connectionInfoBottomSheetBehavior.peekHeight = getBottomSheetPeekHeight()
                    }
                    is PeerConnectionState.ExpandedConnectedToPeer -> {

                    }
                    is PeerConnectionState.ExpandedSearchingForPeer -> {
                        if (!isBottomSheetLayoutConfigured) configureConnectionInfoPersistentBottomSheet()
                        connectionInfoBottomSheetBehavior.isHideable = false
                        connectionInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        collapsedSearchingForPeersInfoBinding.root.alpha = 0f
                        activityMainBinding.connectToPeerButton.alpha = 0f
                        activityMainBinding.sendFileButton.animate().alpha(1f).start()
                        connectionInfoBottomSheetBehavior.peekHeight = getBottomSheetPeekHeight()

                        discoveredPeersRecyclerViewAdapter.submitList(peerConnectionState.devices.map { wifiP2pDevice ->
                            DiscoveredPeersDataItem.DiscoveredPeer(wifiP2pDevice = wifiP2pDevice)
                        }.toMutableList())
                    }

                    PeerConnectionState.NoConnectionAction -> {
                        if (isBottomSheetLayoutConfigured) {
                            connectionInfoBottomSheetBehavior.isHideable = true
                            connectionInfoBottomSheetBehavior.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            activityMainBinding.sendFileButton.animate().alpha(0f).start()
                            activityMainBinding.connectToPeerButton.animate().alpha(1f).start()
                        }
                    }
                }
            }
        }
        // wifiP2p state changed, either enabled or disabled
        mainActivityViewModel.isWifiP2pEnabled.observe(this, {
            it?.let {
                if (it) {
                    // begin peer discovery
                }
            }
        })

        // peeredDevice connection info ready, use this details to create a socket connection btw both device
        mainActivityViewModel.peeredDeviceConnectionInfo.observe(this, {
            it?.let { wifiP2pInfo ->
                wifiP2pInfo.groupOwnerAddress?.let {
                    val ipAddressForServerSocket: String = it.hostAddress
                    if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                        // server
                        if (mainActivityViewModel.serverService.value == null) {
                            Intent(this@MainActivity, ServerService::class.java).apply {
                                bindService(this, serverServiceConnection, Context.BIND_AUTO_CREATE)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startService(this)
                                    startForegroundService(this)
                                } else {
                                    startService(this)
                                }
                            }
                        } else {
                            Toast.makeText(this, "Connected to client already", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (wifiP2pInfo.groupFormed) {
                        // client
                        if (mainActivityViewModel.clientService.value == null) {
                            Intent(this@MainActivity, ClientService::class.java).apply {
                                putExtra(SERVER_IP_ADDRESS_KEY, ipAddressForServerSocket)
                                bindService(this, clientServiceConnection, Context.BIND_AUTO_CREATE)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startService(this)
                                    startForegroundService(this)
                                } else {
                                    startService(this)
                                }
                            }
                        } else Toast.makeText(
                            this,
                            "Connected to server already",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        })
    }

    fun wifiP2pState(isEnabled: Boolean) {
        mainActivityViewModel.wifiP2pStateChange(newState = isEnabled)
    }

    private fun initializeChannelAndBroadcastReceiver() {
        wifiP2pChannel =
            wifiP2pManager.initialize(this, mainLooper, object : WifiP2pManager.ChannelListener {
                // The channel to the framework has been disconnected.
                // Application could try re-initializing
                override fun onChannelDisconnected() {
                }
            })
        // use the activity, wifiP2pManager and wifiP2pChannel to initialize the wifiDiectBroadcastReceiver
        wifiP2pChannel.also { channel: WifiP2pManager.Channel ->
            wifiDirectBroadcastReceiver = WifiDirectBroadcastReceiver(
                mainActivity = this,
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

    // check if SpeedForce has access to read and write to the device external storage
    private fun checkReadAndWriteExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Permission granted to SpeedForce to read and write to the device external storage
            // TODO Go ahead an inform the viewModel to fetch, media items from the repositoris
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE),
                READ_WRITE_STORAGE_REQUEST_CODE
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
        observeViewModelLiveData()
        createNotificationChannel()
        checkReadAndWriteExternalStoragePermission()
        initializeChannelAndBroadcastReceiver()
        intentFilter = registerIntentFilter()
    }

    override fun onResume() {
        super.onResume()
        // register the broadcast receiver
        registerReceiver(wifiDirectBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        // unbind the bounded services
        if (isClientServiceBound) unbindService(clientServiceConnection)
        if (isServerServiceBound) unbindService(serverServiceConnection)
        isServerServiceBound = false
        isClientServiceBound = false
        // unregister the broadcast receiver
        unregisterReceiver(wifiDirectBroadcastReceiver)
    }

    override fun onBackPressed() {

        when (mainActivityViewModel.peerConnectionState.value) {
            is PeerConnectionState.ExpandedSearchingForPeer -> {
                mainActivityViewModel.updatePeerConnectionState(
                    peerConnectionState =
                    PeerConnectionState.CollapsedSearchingForPeer(numberOfDevicesFound = 0)
                )
            }
            is PeerConnectionState.ExpandedConnectedToPeer -> {

            }
            else -> {
                super.onBackPressed()
            }
        }
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


