package com.salesground.zipbolt

import android.Manifest.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.View.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.salesground.zipbolt.broadcast.DataTransferServiceConnectionStateReceiver
import com.salesground.zipbolt.broadcast.SendDataBroadcastReceiver
import com.salesground.zipbolt.databinding.*
import com.salesground.zipbolt.databinding.ActivityMainBinding.inflate
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.PeerConnectionUIState
import com.salesground.zipbolt.notification.FileTransferServiceNotification
import com.salesground.zipbolt.service.DataTransferService
import com.salesground.zipbolt.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayoutMediator
import com.salesground.zipbolt.broadcast.UpgradedWifiDirectBroadcastReceiver
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.ui.AllMediaOnDeviceViewPagerAdapter
import com.salesground.zipbolt.ui.fragments.FilesFragment
import com.salesground.zipbolt.ui.fragments.GroupCreatedFragment
import com.salesground.zipbolt.ui.fragments.PeersDiscoveryFragment
import com.salesground.zipbolt.ui.recyclerview.SentAndReceiveDataItemsViewPagerAdapter
import com.salesground.zipbolt.utils.*
import kotlinx.coroutines.*
import java.util.*


private const val FINE_LOCATION_REQUEST_CODE = 100
const val OPEN_MAIN_ACTIVITY_PENDING_INTENT_REQUEST_CODE = 1010

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    interface PopBackStackListener {
        fun popStack(): Boolean
    }

    private var popBackStackListener: PopBackStackListener? = null

    fun setBackButtonPressedClickListener(popBackStackListener: PopBackStackListener) {
        this.popBackStackListener = popBackStackListener
    }

    enum class DeviceTransferRole {
        SEND,
        RECEIVE,
        SEND_AND_RECEIVE,
        SEND_AND_RECEIVE_BUT_DISCOVERING,
        SEND_BUT_DISCOVERING_PEER,
        RECEIVE_BUT_DISCOVERING_PEER,
        NO_ROLE
    }

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val dataToTransferViewModel: DataToTransferViewModel by viewModels()
    private val sentDataViewModel: SentDataViewModel by viewModels()
    private val receivedDataViewModel: ReceivedDataViewModel by viewModels()

    //fragments
    private var groupCreatedFragment: GroupCreatedFragment? = null
    private var peersDiscoveryFragment: PeersDiscoveryFragment? = null

    @Inject
    lateinit var ftsNotification: FileTransferServiceNotification

    @Inject
    lateinit var wifiP2pManager: WifiP2pManager

    @Inject
    lateinit var wifiManager: WifiManager

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val sendDataClickedIntent =
        Intent(SendDataBroadcastReceiver.ACTION_SEND_DATA_BUTTON_CLICKED)

    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var upgradedWifiDirectBroadcastReceiver: UpgradedWifiDirectBroadcastReceiver
    private var dataTransferServiceIntent: Intent? = null
    private var deviceTransferRole: DeviceTransferRole = DeviceTransferRole.NO_ROLE
    private val turnOnWifiResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                when (deviceTransferRole) {
                    DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING -> {
                        openPeersDiscoveryModalBottomSheet()
                    }
                    DeviceTransferRole.SEND_BUT_DISCOVERING_PEER -> {
                        openGroupCreatedModalBottomSheet()
                    }
                    DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER -> {
                        openPeersDiscoveryModalBottomSheet()
                    }
                }
            }
        }

    private val dataTransferServiceConnectionStateReceiver =
        DataTransferServiceConnectionStateReceiver(object :
            DataTransferServiceConnectionStateReceiver.ConnectionStateListener {
            override fun disconnectedFromPeer() {
                mainActivityViewModel.peerConnectionNoAction()
            }

            override fun cannotConnectToPeerAddress() {
                mainActivityViewModel.peerConnectionNoAction()
            }

            override fun connectionBroken() {

            }
        })

    private val dataTransferServiceDataReceiveListener: DataTransferService.DataFlowListener by lazy {
        object : DataTransferService.DataFlowListener {
            override fun onDataReceive(
                dataDisplayName: String,
                dataSize: Long,
                percentageOfDataRead: Float,
                dataType: Int,
                dataUri: Uri?,
                dataTransferStatus: DataToTransfer.TransferStatus
            ) {
                lifecycleScope.launch(Dispatchers.Main) {
                    when (dataTransferStatus) {
                        DataToTransfer.TransferStatus.RECEIVE_STARTED -> {
                            // expand the bottom sheet to show receive has started
                            mainActivityViewModel.expandedConnectedToPeerReceiveOngoing()
                            receivedDataViewModel.onDataReceiveStarted(
                                ReceivedDataItem(
                                    dataDisplayName,
                                    dataSize,
                                    percentageOfDataRead,
                                    dataType,
                                    dataUri
                                )
                            )
                        }

                        DataToTransfer.TransferStatus.RECEIVE_ONGOING -> {
                            receivedDataViewModel.updateOngoingReceiveDataItemReceivePercent(
                                percentageOfDataRead
                            )
                        }

                        DataToTransfer.TransferStatus.RECEIVE_COMPLETE -> {
                            receivedDataViewModel.addDataToReceivedItems(
                                when (dataType) {
                                    MediaType.Image.value -> {
                                        DataToTransfer.DeviceImage(
                                            imageId = 0L,
                                            imageUri = dataUri!!,
                                            imageDateModified = "",
                                            imageMimeType = "image/*",
                                            imageSize = dataSize,
                                            imageBucketName = "ZipBolt Images",
                                            imageDisplayName = dataDisplayName
                                        )
                                    }
                                    MediaType.Video.value -> {
                                        DataToTransfer.DeviceVideo(
                                            videoId = 0L,
                                            videoUri = dataUri!!,
                                            videoDisplayName = dataDisplayName,
                                            videoDuration = dataUri.getVideoDuration(this@MainActivity),
                                            videoSize = dataSize
                                        )
                                    }
                                    MediaType.Audio.value -> {
                                        DataToTransfer.DeviceAudio(
                                            audioUri = dataUri!!,
                                            audioDisplayName = dataDisplayName,
                                            audioSize = dataSize,
                                            audioDuration = dataUri.getAudioDuration(this@MainActivity),
                                            audioArtPath = Uri.parse("")
                                        )
                                    }
                                    MediaType.App.value -> {
                                        DataToTransfer.DeviceApplication(
                                            applicationName = dataDisplayName,
                                            apkPath = dataUri!!.path ?: "",
                                            appSize = dataSize,
                                            applicationIcon = try {
                                                dataUri?.path!!.let { path ->
                                                    packageManager.getPackageArchiveInfo(path, 0)
                                                        .let { packageInfo ->
                                                            packageManager.getApplicationIcon(
                                                                packageInfo!!
                                                                    .applicationInfo.apply {
                                                                        sourceDir = path
                                                                        publicSourceDir = path
                                                                    })
                                                        }
                                                }
                                            } catch (nullPointerException: NullPointerException) {
                                                null
                                            }
                                        ).apply {
                                            this.dataType = dataType
                                        }
                                    }
                                    in MediaType.File.ImageFile.value
                                            ..MediaType.File.Document.DatDocument.value -> {
                                        DataToTransfer.DeviceFile(
                                            dataUri!!.toFile()
                                        ).apply {
                                            this.dataType = dataType
                                        }
                                    }

                                    else -> {
                                        DataToTransfer.DeviceImage(
                                            imageId = 0L,
                                            imageUri = dataUri!!,
                                            imageDateModified = "",
                                            imageMimeType = "image/*",
                                            imageSize = dataSize,
                                            imageBucketName = "ZipBolt Images",
                                            imageDisplayName = dataDisplayName
                                        )
                                    }
                                }.apply {
                                    transferStatus = DataToTransfer.TransferStatus.RECEIVE_COMPLETE
                                }
                            )
                        }
                    }
                }
            }

            override fun totalFileReceiveComplete() {
                mainActivityViewModel.totalFileReceiveComplete()
            }

            override fun onDataTransfer(
                dataToTransfer: DataToTransfer,
                percentTransferred: Float,
                transferStatus: DataToTransfer.TransferStatus
            ) {
                when (transferStatus) {
                    DataToTransfer.TransferStatus.TRANSFER_STARTED -> {
                        sentDataViewModel.changeCurrentDataToTransferDataItem(dataToTransfer)
                        sentDataViewModel.setCurrentDataToTransferPercentTransferred(
                            percentTransferred
                        )
                    }
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                        sentDataViewModel.dataTransferCompleted(dataToTransfer)
                        sentDataViewModel.setCurrentDataToTransferPercentTransferred(
                            percentTransferred
                        )
                    }
                    DataToTransfer.TransferStatus.TRANSFER_ONGOING -> {
                        sentDataViewModel.setCurrentDataToTransferPercentTransferred(
                            percentTransferred
                        )
                    }
                    DataToTransfer.TransferStatus.TRANSFER_CANCELLED -> {
                        // from the cancelled media item from the queue of data in transfer
                        sentDataViewModel.cancelDataTransfer(dataToTransfer)
                    }
                    DataToTransfer.TransferStatus.NO_ACTION -> {

                    }
                    DataToTransfer.TransferStatus.TRANSFER_WAITING -> {

                    }
                }
            }
        }
    }

    // ui variables
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var connectionOptionsBottomSheetDialog: BottomSheetDialog
    private var isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured: Boolean = false

    private val connectedToPeerTransferOngoingBottomSheetLayoutBinding:
            ConnectedToPeerTransferOngoingPersistentBottomSheetBinding by lazy {
        MainActivityDataBindingUtils.getConnectedToPeerTransferOngoingPersistentBottomSheetBinding(
            this
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
            service.getServiceInstance()
                .setOnDataReceiveListener(dataTransferServiceDataReceiveListener)
            dataTransferService = service.getServiceInstance()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            displayToast("Service disconnected")
            dataTransferService = null
        }
    }

    private val wifiDirectBroadcastReceiverCallback = object :
        UpgradedWifiDirectBroadcastReceiver.WifiDirectBroadcastReceiverCallback {
        override fun wifiOn() {
            if (deviceTransferRole == DeviceTransferRole.SEND_BUT_DISCOVERING_PEER
                && groupCreatedFragment == null
            ) {
                openGroupCreatedModalBottomSheet()
            } else if ((deviceTransferRole == DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER
                        || deviceTransferRole == DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING)
                && peersDiscoveryFragment == null
            ) {
                openPeersDiscoveryModalBottomSheet()
            }
        }

        override fun wifiOff() {

        }


        override fun connectedToPeer(
            wifiP2pInfo: WifiP2pInfo,
            peeredDevice: WifiP2pDevice
        ) {
            deviceTransferRole = when (deviceTransferRole) {
                DeviceTransferRole.SEND_BUT_DISCOVERING_PEER -> {
                    closeGroupCreatedModalBottomSheet()
                    DeviceTransferRole.SEND
                }
                DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER -> {
                    closePeersDiscoveryModalBottomSheet()
                    DeviceTransferRole.RECEIVE
                }
                DeviceTransferRole.NO_ROLE -> {
                    DeviceTransferRole.NO_ROLE
                }
                DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING -> {
                    closePeersDiscoveryModalBottomSheet()
                    DeviceTransferRole.SEND_AND_RECEIVE
                }
                else -> {
                    deviceTransferRole
                }
            }

            mainActivityViewModel.collapsedConnectedToPeerTransferOngoing()
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
                            dataTransferServiceIntent = serviceIntent.apply {
                                putExtra(DataTransferService.IS_SERVER, true)
                                putExtra(
                                    DataTransferService.IS_ONE_DIRECTIONAL_TRANSFER,
                                    deviceTransferRole
                                            != DeviceTransferRole.SEND_AND_RECEIVE
                                )
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(
                                    this@MainActivity,
                                    serviceIntent
                                )
                            } else {
                                startService(serviceIntent)
                            }
                            bindService(
                                serviceIntent,
                                dataTransferServiceConnection,
                                BIND_AUTO_CREATE
                            )
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
                            dataTransferServiceIntent = serviceIntent.apply {
                                putExtra(DataTransferService.IS_SERVER, false)
                                putExtra(DataTransferService.SERVER_IP_ADDRESS, serverIpAddress)
                                putExtra(DataTransferService.IS_ONE_DIRECTIONAL_TRANSFER, true)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(
                                    this@MainActivity,
                                    serviceIntent
                                )
                            } else {
                                startService(serviceIntent)
                            }
                            bindService(
                                serviceIntent,
                                dataTransferServiceConnection,
                                BIND_AUTO_CREATE
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        inflate(layoutInflater).apply {
            activityMainBinding = this
            connectToPeerButton.setOnClickListener {
                if (it.visibility == VISIBLE) {
                    configureConnectionOptionsModalBottomSheetLayout()
                    connectionOptionsBottomSheetDialog.show()
                }
            }

            sendFileButton.setOnClickListener {
                if (it.visibility != INVISIBLE) {
                    // send broadcast event that send data button has been triggered
                    localBroadcastManager.sendBroadcast(sendDataClickedIntent)

                    mainActivityViewModel.expandedConnectedToPeerTransferOngoing()
                    // transfer data using the DataTransferService
                    dataTransferService?.transferData(
                        dataToTransferViewModel.collectionOfDataToTransfer
                    )
                    sentDataViewModel.addCollectionOfDataToTransferToSentDataItems(
                        dataToTransferViewModel.collectionOfDataToTransfer
                    )
                    // clear collection of data to transfer since transfer has been completed
                    dataToTransferViewModel.clearCollectionOfDataToTransfer()
                    dataToTransferViewModel.sentDataButtonClicked()
                }
            }

            mainActivityAllMediaOnDevice.run {
                // change the tab mode based on the current screen density
                allMediaOnDeviceTabLayout.tabMode = if (resources.configuration.fontScale > 1.1) {
                    TabLayout.MODE_SCROLLABLE
                } else TabLayout.MODE_FIXED

                tabLayoutViewPagerConfiguration(
                    allMediaOnDeviceTabLayout,
                    allMediaOnDeviceViewPager,
                    AllMediaOnDeviceViewPagerAdapter(supportFragmentManager),
                    "Apps",
                    "Images",
                    "Videos",
                    "Music",
                    "Files"
                )
            }
            setContentView(root)
        }
        lifecycle.apply {
            addObserver(ftsNotification)
        }
        observeViewModelLiveData()
        initializeChannelAndBroadcastReceiver()
        // bind to the data transfer service
        Intent(this, DataTransferService::class.java).also {
            bindService(it, dataTransferServiceConnection, BIND_AUTO_CREATE)
        }
        PermissionUtils.checkReadAndWriteExternalStoragePermission(this)
        registerReceiver(upgradedWifiDirectBroadcastReceiver, createSystemBroadcastIntentFilter())
        localBroadcastManager.registerReceiver(
            dataTransferServiceConnectionStateReceiver,
            IntentFilter().apply {
                addAction(DataTransferServiceConnectionStateReceiver.ACTION_DISCONNECTED_FROM_PEER)
                addAction(DataTransferServiceConnectionStateReceiver.ACTION_CANNOT_CONNECT_TO_PEER_ADDRESS)
            }
        )
    }

    private fun tabLayoutViewPagerConfiguration(
        tabLayout: TabLayout, viewPager: ViewPager,
        viewPagerAdapter: FragmentStatePagerAdapter,
        vararg tabNames: String
    ) {
        for (tabName in tabNames) {
            tabLayout.addTab(tabLayout.newTab().setText(tabName))
        }

        viewPager.adapter = viewPagerAdapter
        viewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(tabLayout)
        )

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager.currentItem = tab.position
                }
            }
        })
    }

    private fun observeViewModelLiveData() {
        mainActivityViewModel.peerConnectionUIState.observe(this) {
            it?.let {
                when (it) {
                    is PeerConnectionUIState.CollapsedConnectedToPeerTransferOngoing -> {
                        if (deviceTransferRole == DeviceTransferRole.SEND ||
                            deviceTransferRole == DeviceTransferRole.SEND_AND_RECEIVE
                        ) {
                            activityMainBinding.run {
                                sendFileButton.visibility = VISIBLE
                                connectToPeerButton.visibility = INVISIBLE
                            }
                        }
                        if (!isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured) {
                            configureConnectedToPeerTransferOngoingBottomSheetLayout()
                        }
                        connectedToPeerTransferOngoingBottomSheetBehavior.apply {
                            state = BottomSheetBehavior.STATE_COLLAPSED
                            peekHeight = getBottomSheetPeekHeight()
                        }
                    }

                    is PeerConnectionUIState.ExpandedConnectedToPeerTransferOngoing -> {
                        if (!isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured) {
                            configureConnectedToPeerTransferOngoingBottomSheetLayout()
                        }
                        with(connectedToPeerTransferOngoingBottomSheetBehavior) {
                            state =
                                BottomSheetBehavior.STATE_EXPANDED
                            peekHeight =
                                getBottomSheetPeekHeight()
                        }
                    }

                    PeerConnectionUIState.NoConnectionUIAction -> {
                        if (isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured) {
                            connectedToPeerTransferOngoingBottomSheetBehavior.isHideable = true
                            connectedToPeerTransferOngoingBottomSheetBehavior.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured = false
                        }
                        activityMainBinding.run {
                            sendFileButton.visibility = INVISIBLE
                            connectToPeerButton.visibility = VISIBLE
                        }
                    }
                }
            }
        }
    }


    private fun configureConnectedToPeerTransferOngoingBottomSheetLayout() {
        isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured = true
        val sentAndReceivedDataItemsViewPagerAdapter = SentAndReceiveDataItemsViewPagerAdapter(
            supportFragmentManager, lifecycle, when (deviceTransferRole) {
                DeviceTransferRole.SEND -> true
                DeviceTransferRole.RECEIVE -> false
                DeviceTransferRole.SEND_AND_RECEIVE -> true
                else -> true
            }
        )
        connectedToPeerTransferOngoingBottomSheetLayoutBinding.run {
            // configure collapsed connected to peer transfer ongoing layout
            collapsedConnectedToPeerOngoingDataTransferLayout.run {
                root.setOnClickListener {
                    connectedToPeerTransferOngoingBottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_EXPANDED
                }
                collapsedConnectedToPeerOngoingTransferQuitTransferButton.setOnClickListener {
                    breakConnection()
                }
            }

            // configure expanded connected to peer transfer ongoing layout
            expandedConnectedToPeerTransferOngoingLayout.run {
                root.alpha = 0f
                expandedConnectedToPeerTransferOngoingToolbar.run {
                    expandedBottomSheetLayoutToolbarTitleTextView.text =
                        getString(R.string.transfer_history)
                    expandedBottomSheetLayoutToolbarCancelButton.setOnClickListener {
                        // close the connection with the peer
                        breakConnection()
                    }
                    expandedBottomSheetLayoutToolbarCollapseBottomSheetButton.setOnClickListener {
                        // collapse the connected to peer transfer ongoing bottom sheet
                        connectedToPeerTransferOngoingBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_COLLAPSED
                        connectedToPeerTransferOngoingBottomSheetBehavior.peekHeight =
                            getBottomSheetPeekHeight()
                    }
                }
                expandedConnectedToPeerTransferOngoingViewPager2.adapter =
                    sentAndReceivedDataItemsViewPagerAdapter
                TabLayoutMediator(
                    expandedConnectedToPeerTransferOngoingTabLayout,
                    expandedConnectedToPeerTransferOngoingViewPager2
                ) { tab, position ->
                    tab.text = when (deviceTransferRole) {
                        DeviceTransferRole.SEND -> {
                            if (position == 0) {
                                "Sent"
                            } else {
                                "Received"
                            }
                        }
                        DeviceTransferRole.RECEIVE -> {
                            if (position == 0) {
                                "Received"
                            } else {
                                "Sent"
                            }
                        }
                        else -> {
                            if (position == 0) {
                                "Sent"
                            } else {
                                "Received"
                            }
                        }
                    }
                }.attach()
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
                    else -> {
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

    private fun configureConnectionOptionsModalBottomSheetLayout() {
        connectionOptionsBottomSheetDialog = BottomSheetDialog(this)
        connectionOptionsBottomSheetDialog.setContentView(
            ZipBoltProConnectionOptionsBottomSheetLayoutBinding.inflate(layoutInflater).apply {
                zipBoltProConnectionOptionsBottomSheetLayoutSendCardView.setOnClickListener {
                    deviceTransferRole = DeviceTransferRole.SEND_BUT_DISCOVERING_PEER
                    // Turn on device wifi if it is off
                    if (!wifiManager.isWifiEnabled) {
                        toggleWifi(false)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            turnOnWifiResultLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
                        } else {
                            if (wifiManager.setWifiEnabled(true)) {
                                /**Listen for wifi on via the broadcast receiver
                                 * and then call openGroupCreatedModalBottomSheet**/
                            } else {
                                displayToast("Turn off your hotspot")
                            }
                        }
                    } else {
                        if (isLocationPermissionGranted()) {
                            // Create wifi p2p group, if wifi is enabled
                            openGroupCreatedModalBottomSheet()
                        } else {
                            requestFineLocationPermission()
                        }
                    }
                    connectionOptionsBottomSheetDialog.dismiss()
                }
                zipBoltProConnectionOptionsBottomSheetLayoutReceiveCardView.setOnClickListener {
                    deviceTransferRole = DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER
                    // Turn on device wifi
                    if (!wifiManager.isWifiEnabled) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            turnOnWifiResultLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
                        } else {
                            if (wifiManager.setWifiEnabled(true)) {
                                /**Listen for wifi on via the broadcast receiver
                                 * and then call openPeersDiscoveryModalBottomSheet**/

                            } else {
                                displayToast("Turn off your hotspot")
                            }
                        }
                    } else {
                        if (isLocationPermissionGranted()) {
                            // Create wifi p2p group, if wifi is enabled
                            openPeersDiscoveryModalBottomSheet()
                        } else {
                            requestFineLocationPermission()
                        }
                    }
                    connectionOptionsBottomSheetDialog.hide()
                }

                zipBoltProConnectionOptionsBottomSheetLayoutSendAndReceiveCardView.setOnClickListener {
                    deviceTransferRole = DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING
                }
            }.root
        )
    }

    private fun openGroupCreatedModalBottomSheet() {
        groupCreatedFragment = GroupCreatedFragment.newInstance()
        groupCreatedFragment?.isCancelable = false
        groupCreatedFragment?.show(
            supportFragmentManager,
            "GroupCreatedBottomSheetFragment"
        )
    }

    private fun openPeersDiscoveryModalBottomSheet() {
        peersDiscoveryFragment = PeersDiscoveryFragment.newInstance()
        peersDiscoveryFragment?.isCancelable = false
        peersDiscoveryFragment?.show(
            supportFragmentManager,
            "PeersDiscoveryBottomSheetFragment"
        )
    }

    fun closeGroupCreatedModalBottomSheet() {
        groupCreatedFragment?.dismiss()
        groupCreatedFragment = null
    }

    fun closePeersDiscoveryModalBottomSheet() {
        peersDiscoveryFragment?.dismiss()
        peersDiscoveryFragment = null
    }

    fun cancelOngoingDataTransfer() {
        dataTransferService?.cancelActiveTransfer()
    }

    fun cancelOngoingDataReceive() {
        dataTransferService?.cancelActiveReceive()
    }

    private fun breakConnection() {
        dataTransferService?.killDataTransferService()
        // remove the wifi p2p group
        wifiP2pManager.removeGroup(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                mainActivityViewModel.peerConnectionNoAction()
            }

            override fun onFailure(reason: Int) {
                mainActivityViewModel.peerConnectionNoAction()
            }
        })
        toggleWifi(false)
        // turn of wifi if it is on
    }

    private fun toggleWifi(state: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            wifiManager.isWifiEnabled = state
        } else {
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
            }.launch(
                Intent(
                    Settings.Panel.ACTION_WIFI
                )
            )
        }
    }


    fun addToDataToTransferList(dataToTransfer: DataToTransfer) {
        dataToTransferViewModel.addDataToTransfer(dataToTransfer)
    }

    fun removeFromDataToTransferList(dataToTransfer: DataToTransfer) {
        dataToTransferViewModel.removeDataFromDataToTransfer(dataToTransfer)
    }


    private fun getBottomSheetPeekHeight(): Int {
        return (60 * resources.displayMetrics.density).roundToInt()
    }


    private fun createSystemBroadcastIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        }
    }


    @SuppressLint("MissingPermission")
    private fun initializeChannelAndBroadcastReceiver() {
        wifiP2pChannel =
            wifiP2pManager.initialize(
                this, mainLooper
            ) {

            }
        // use the activity, wifiP2pManager and wifiP2pChannel to initialize the wifiDiectBroadcastReceiver

        upgradedWifiDirectBroadcastReceiver = UpgradedWifiDirectBroadcastReceiver(
            wifiDirectBroadcastReceiverCallback = wifiDirectBroadcastReceiverCallback,
            wifiP2pManager = wifiP2pManager,
            wifiP2pChannel = wifiP2pChannel
        )
        wifiP2pManager.removeGroup(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {

            }

            override fun onFailure(reason: Int) {

            }

        })
    }

    // check if SpeedForce has access to device fine location
    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.ACCESS_FINE_LOCATION),
            FINE_LOCATION_REQUEST_CODE
        )
    }


    private fun isLocationPermissionGranted() = ActivityCompat.checkSelfPermission(
        this,
        permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE && permissions.contains(permission.ACCESS_FINE_LOCATION)) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when (deviceTransferRole) {
                        DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING -> {
                            openPeersDiscoveryModalBottomSheet()
                        }
                        DeviceTransferRole.SEND_BUT_DISCOVERING_PEER -> {
                            openGroupCreatedModalBottomSheet()
                        }
                        DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER -> {
                            openPeersDiscoveryModalBottomSheet()
                        }
                        else -> {
                        }
                    }
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

    override fun onDestroy() {
        super.onDestroy()
        unbindService(dataTransferServiceConnection)
        // unregister the broadcast receiver
        unregisterReceiver(upgradedWifiDirectBroadcastReceiver)
        localBroadcastManager.unregisterReceiver(dataTransferServiceConnectionStateReceiver)

    }

    override fun onBackPressed() {
        if (isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured) {
            if (connectedToPeerTransferOngoingBottomSheetBehavior.state ==
                BottomSheetBehavior.STATE_EXPANDED
            ) {
                connectedToPeerTransferOngoingBottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED
                return
            }
        }

        if (FilesFragment.backStackCount > 0) {
            if (popBackStackListener?.popStack() == true) {

            } else {
                super.onBackPressed()
            }
        } else super.onBackPressed()
    }
}