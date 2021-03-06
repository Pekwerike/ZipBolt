package com.salesground.zipbolt

import android.Manifest
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
import android.content.res.Configuration
import android.provider.Settings
import android.view.WindowInsetsController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayoutMediator
import com.salesground.zipbolt.broadcast.UpgradedWifiDirectBroadcastReceiver
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.ui.AllMediaOnDeviceViewPagerAdapter
import com.salesground.zipbolt.ui.fragments.FilesFragment
import com.salesground.zipbolt.ui.fragments.modalbottomsheets.GroupCreatedBottomSheetFragment
import com.salesground.zipbolt.ui.fragments.modalbottomsheets.PeersDiscoveryBottomSheetFragment
import com.salesground.zipbolt.ui.fragments.modalbottomsheets.SendAndReceiveBottomSheetFragment
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
    private val generalViewModel: GeneralViewModel by viewModels()

    //fragments
    private var groupCreatedBottomSheetFragment: GroupCreatedBottomSheetFragment? = null
    private var peersDiscoveryBottomSheetFragment: PeersDiscoveryBottomSheetFragment? =
        null
    private var sendAndReceiveBottomSheetFragment: SendAndReceiveBottomSheetFragment? = null


    @Inject
    lateinit var ftsNotification: FileTransferServiceNotification

    @Inject
    lateinit var wifiP2pManager: WifiP2pManager

    @Inject
    lateinit var wifiManager: WifiManager

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

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
                when (dataTransferStatus) {
                    DataToTransfer.TransferStatus.RECEIVE_STARTED -> {
                        // expand the bottom sheet to show receive has started
                        lifecycleScope.launch(Dispatchers.Main) {
                            mainActivityViewModel.expandedConnectedToPeerReceiveOngoing()
                        }
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
            dataTransferService = null
        }
    }

    private val wifiDirectBroadcastReceiverCallback = object :
        UpgradedWifiDirectBroadcastReceiver.WifiDirectBroadcastReceiverCallback {
        override fun wifiOn() {
            if (deviceTransferRole == DeviceTransferRole.SEND_BUT_DISCOVERING_PEER
                && groupCreatedBottomSheetFragment == null
            ) {
            //    openGroupCreatedModalBottomSheet()
            } else if (deviceTransferRole == DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER && peersDiscoveryBottomSheetFragment == null) {
                preparePeerDiscovery()
            } else if (deviceTransferRole == DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING &&
                sendAndReceiveBottomSheetFragment == null
            ) {
                openSendAndReceiveModalBottomSheet()
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
                    closeSendAndReceiveModalBottomSheet()
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
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        inflate(layoutInflater).apply {
            activityMainBinding = this
            configureZipBoltHeader()
            mainActivityAllMediaOnDevice.run {
                // change the tab mode based on the current screen density
                allMediaOnDeviceTabLayout.tabMode =
                    if (resources.configuration.fontScale > 1.1) {
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
        checkReadAndWriteExternalStoragePermission()
        registerReceiver(
            upgradedWifiDirectBroadcastReceiver,
            createSystemBroadcastIntentFilter()
        )
        localBroadcastManager.registerReceiver(
            dataTransferServiceConnectionStateReceiver,
            IntentFilter().apply {
                addAction(DataTransferServiceConnectionStateReceiver.ACTION_DISCONNECTED_FROM_PEER)
                addAction(DataTransferServiceConnectionStateReceiver.ACTION_CANNOT_CONNECT_TO_PEER_ADDRESS)
            }
        )
    }

    private fun configureZipBoltHeader() {
        activityMainBinding.run {
            activityMainZipBoltHeaderLayout.run {
                zipBoltHeaderLayoutConnectToPeerButton.setOnClickListener {
                    if (it.visibility == VISIBLE) {
                        configureConnectionOptionsModalBottomSheetLayout()
                        connectionOptionsBottomSheetDialog.show()
                    }
                }
            }
            activityMainZipBoltFilesTransferSelectedFilesHeaderLayout.run {
                zipBoltSendFileHeaderLayoutDropAllItemsButton.setOnClickListener {
                    dataToTransferViewModel.dropAllSelectedItems()
                }
                zipBoltSendFileHeaderLayoutSendFileButton.setOnClickListener {
                    setUpTransfer()
                }
            }
        }
    }

    private fun setUpTransfer() {
        mainActivityViewModel.expandedConnectedToPeerTransferOngoing()
        // transfer data using the DataTransferService
        dataTransferService?.transferData(
            dataToTransferViewModel.collectionOfDataToTransfer
        )
        sentDataViewModel.addCollectionOfDataToTransferToSentDataItems(
            dataToTransferViewModel.collectionOfDataToTransfer
        )
        // clear collection of data to transfer since transfer has been completed
        dataToTransferViewModel.dropAllSelectedItems()
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
                        activityMainBinding.run {
                            activityMainZipBoltHeaderLayout
                                .zipBoltHeaderLayoutConnectToPeerButton.visibility = INVISIBLE

                            activityMainZipBoltFilesTransferSelectedFilesHeaderLayout.run {
                                zipBoltSendFileHeaderLayoutSendFileButton.run {
                                    text = getString(R.string.send_label)
                                    visibility =
                                        if (deviceTransferRole == DeviceTransferRole.RECEIVE) {
                                            INVISIBLE
                                        } else {
                                            VISIBLE
                                        }
                                    setOnClickListener {
                                        setUpTransfer()
                                    }
                                }
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
                        configureZipBoltHeader()
                        activityMainBinding.run {
                            activityMainZipBoltHeaderLayout
                                .zipBoltHeaderLayoutConnectToPeerButton.visibility = VISIBLE
                        }
                        if (isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured) {
                            connectedToPeerTransferOngoingBottomSheetBehavior.isHideable = true
                            connectedToPeerTransferOngoingBottomSheetBehavior.state =
                                BottomSheetBehavior.STATE_HIDDEN
                            isConnectedToPeerTransferOngoingBottomSheetLayoutConfigured = false
                        }
                    }
                }
            }
        }
        dataToTransferViewModel.collectionOfDataToTransferLiveData.observe(this) { selectedItemsList ->
            selectedItemsList?.let {
                activityMainBinding.run {
                    if (deviceTransferRole == DeviceTransferRole.SEND_AND_RECEIVE
                        || deviceTransferRole == DeviceTransferRole.SEND
                    ) {
                        // device connected
                        if (selectedItemsList.isNotEmpty()) {
                            turnStatusBarColor(true)
                            activityMainZipBoltHeaderLayout.root.visibility = INVISIBLE
                            activityMainZipBoltFilesTransferSelectedFilesHeaderLayout.run {
                                numberOfFilesSelected = it.size
                                sizeOfFileSelected = it.sumOf { it.dataSize }
                                root.visibility = VISIBLE
                                zipBoltSendFileHeaderLayoutSendFileButton.run {
                                    text = getString(R.string.send_label)
                                }
                            }
                        } else {
                            turnStatusBarColor(false)
                            activityMainZipBoltFilesTransferSelectedFilesHeaderLayout.root.visibility =
                                INVISIBLE
                            activityMainZipBoltHeaderLayout.run {
                                root.visibility = VISIBLE
                                zipBoltHeaderLayoutConnectToPeerButton.visibility = INVISIBLE
                            }
                        }
                    } else {
                        // device not connected
                        if (selectedItemsList.isNotEmpty()) {
                            turnStatusBarColor(true)
                            activityMainZipBoltHeaderLayout.root.visibility = INVISIBLE
                            activityMainZipBoltFilesTransferSelectedFilesHeaderLayout.run {
                                root.visibility = VISIBLE
                                numberOfFilesSelected = it.size
                                sizeOfFileSelected = it.sumOf { it.dataSize }
                                zipBoltSendFileHeaderLayoutSendFileButton.run {
                                    visibility =
                                        if (deviceTransferRole == DeviceTransferRole.RECEIVE) {
                                            INVISIBLE
                                        } else {
                                            VISIBLE
                                        }
                                    text = getString(R.string.connect)
                                    setOnClickListener {
                                        // show connection options modal bottom sheet
                                        configureConnectionOptionsModalBottomSheetLayout()
                                        connectionOptionsBottomSheetDialog.show()
                                    }
                                }
                            }
                        } else {
                            turnStatusBarColor(false)
                            activityMainZipBoltFilesTransferSelectedFilesHeaderLayout.root.visibility =
                                INVISIBLE
                            activityMainZipBoltHeaderLayout.run {
                                root.visibility = VISIBLE
                                zipBoltHeaderLayoutConnectToPeerButton.visibility =
                                    if (deviceTransferRole == DeviceTransferRole.RECEIVE) {
                                        INVISIBLE
                                    } else {
                                        VISIBLE
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun turnStatusBarColor(dark: Boolean) {
        if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            if (dark) {
                window.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        insetsController?.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    } else {
                        decorView.systemUiVisibility =
                            SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and decorView.systemUiVisibility
                    }
                    statusBarColor =
                        ContextCompat.getColor(this@MainActivity, R.color.dark_status_bar_color)
                }
            } else {
                window.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        insetsController?.setSystemBarsAppearance(
                            0,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    } else {
                        decorView.systemUiVisibility =
                            SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or decorView.systemUiVisibility
                    }
                    statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.white)
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
                        // toggleWifi(false)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            turnOnWifiResultLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
                        } else {
                            if (wifiManager.setWifiEnabled(true)) {
                                /**Listen for wifi on via the broadcast receiver
                                 * and then call openGroupCreatedModalBottomSheet**/
                                openGroupCreatedModalBottomSheet()
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
                    connectionOptionsBottomSheetDialog.hide()
                    preparePeerDiscovery()
                }

                zipBoltProConnectionOptionsBottomSheetLayoutSendAndReceiveCardView.setOnClickListener {
                    deviceTransferRole = DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING
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
                            openSendAndReceiveModalBottomSheet()
                        } else {
                            requestFineLocationPermission()
                        }
                    }
                    connectionOptionsBottomSheetDialog.hide()
                }
            }.root
        )
    }

    private fun openSendAndReceiveModalBottomSheet() {
        sendAndReceiveBottomSheetFragment = SendAndReceiveBottomSheetFragment()
        sendAndReceiveBottomSheetFragment?.show(
            supportFragmentManager,
            "SendAndReceiveBottomSheetFragment"
        )
        sendAndReceiveBottomSheetFragment?.isCancelable = false
    }

    private fun openGroupCreatedModalBottomSheet() {
        groupCreatedBottomSheetFragment = GroupCreatedBottomSheetFragment()
        groupCreatedBottomSheetFragment?.show(
            supportFragmentManager,
            "GroupCreatedBottomSheetFragment"
        )
        groupCreatedBottomSheetFragment?.isCancelable = false
    }

    private fun openPeersDiscoveryModalBottomSheet() {
        peersDiscoveryBottomSheetFragment = PeersDiscoveryBottomSheetFragment()
        peersDiscoveryBottomSheetFragment?.show(
            supportFragmentManager,
            "PeersDiscoveryBottomSheetFragment"
        )
        peersDiscoveryBottomSheetFragment?.isCancelable = false
    }

    fun closeGroupCreatedModalBottomSheet() {
        groupCreatedBottomSheetFragment?.let {
            groupCreatedBottomSheetFragment!!.dismiss()
            supportFragmentManager.beginTransaction()
                .remove(groupCreatedBottomSheetFragment!!)
                .commitAllowingStateLoss()
            groupCreatedBottomSheetFragment = null
        }
    }

    fun closePeersDiscoveryModalBottomSheet() {
        peersDiscoveryBottomSheetFragment?.let {
            peersDiscoveryBottomSheetFragment!!.endServiceDiscovery()

            peersDiscoveryBottomSheetFragment!!.dismiss()
            supportFragmentManager.beginTransaction()
                .remove(peersDiscoveryBottomSheetFragment!!)
                .commit()
            peersDiscoveryBottomSheetFragment = null
        }
    }

    fun closeSendAndReceiveModalBottomSheet() {
        sendAndReceiveBottomSheetFragment?.let {
            sendAndReceiveBottomSheetFragment!!.dismiss()
            supportFragmentManager.beginTransaction()
                .remove(sendAndReceiveBottomSheetFragment!!)
                .commitAllowingStateLoss()
            sendAndReceiveBottomSheetFragment = null
        }
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

    fun checkReadAndWriteExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            generalViewModel.hasPermissionToFetchMedia(true)
        } else {
            generalViewModel.hasPermissionToFetchMedia(false)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.READ_EXTERNAL_STORAGE
                ),
                PermissionUtils.READ_WRITE_STORAGE_REQUEST_CODE
            )
        }
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

    private fun preparePeerDiscovery() {
        deviceTransferRole = DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER
        if (isLocationPermissionGranted()) {
            if (!wifiManager.isWifiEnabled) {
                // turn on wifi
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
                openPeersDiscoveryModalBottomSheet()
            }
        } else {
            requestFineLocationPermission()
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
                    when (deviceTransferRole) {
                        DeviceTransferRole.SEND_AND_RECEIVE_BUT_DISCOVERING -> {
                            openPeersDiscoveryModalBottomSheet()
                        }
                        DeviceTransferRole.SEND_BUT_DISCOVERING_PEER -> {
                            openGroupCreatedModalBottomSheet()
                        }
                        DeviceTransferRole.RECEIVE_BUT_DISCOVERING_PEER -> {
                            preparePeerDiscovery()
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
            checkReadAndWriteExternalStoragePermission()
            /*if (grantResults.isNotEmpty()) {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // SpeedForce has permission to read and write ot the device external storage
                    generalViewModel.hasPermissionToFetchMedia(true)
                }
            }else{

            }*/
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataTransferService?.killDataTransferService()
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