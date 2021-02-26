package com.salesground.zipbolt


import android.Manifest.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.core.app.ActivityCompat
import com.salesground.zipbolt.broadcast.WifiDirectBroadcastReceiver
import com.salesground.zipbolt.localnetwork.Client
import com.salesground.zipbolt.localnetwork.Server
import com.salesground.zipbolt.ui.screen.HomeScreen
import com.salesground.zipbolt.ui.theme.SpeedForceTheme
import com.salesground.zipbolt.viewmodel.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val FINE_LOCATION_REQUEST_CODE = 100
private const val READ_WRITE_STORAGE_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel by viewModels<MainActivityViewModel>()
    private val wifiP2pManager: WifiP2pManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var wifiDirectBroadcastReceiver: WifiDirectBroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedForceTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen(
                        mainActivityViewModel = mainActivityViewModel,
                        sendAction = { beginPeerDiscovery() },
                        receiveAction = { beginPeerDiscovery() })
                }
            }
        }
        checkReadAndWriteExternalStoragePermission()
        observeViewModelLiveData()
        initializeChannelAndBroadcastReceiver()
        intentFilter = registerIntentFilter()
    }

    fun peeredDeviceConnectionInfoReady(deviceConnectionInfo: WifiP2pInfo) {
        mainActivityViewModel.peeredDeviceConnectionInfoUpdated(connectionInfo = deviceConnectionInfo)
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
                    // Broadcast receiveer notifies us in WIFI_P2P_CONNECTION_CHANGED_ACTION
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
                    displayToast("Peer discovery successfully initiated")
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

    private fun displayToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun registerIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
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

    private fun observeViewModelLiveData() {
        // wifiP2p state changed, either enabled or disabled
        mainActivityViewModel.isWifiP2pEnabled.observe(this, {
            it?.let {
                if (it) {
                    // begin peer discovery
                }
            }
        })

        // peeredDevice connection info ready, use this details to create a socket connection btw both device
     /*   mainActivityViewModel.peeredDeviceConnectionInfo.observe(this, {
            it?.let {
                val ipAddressForServerSocket: String = it.groupOwnerAddress.hostAddress
                CoroutineScope(Dispatchers.Main).launch {
                    if (it.groupFormed && it.isGroupOwner) {
                        // kick of the server
                        Server().listenIncomingConnection(this@MainActivity)
                    } else if (it.groupFormed) {
                        // kick of the client, client will connect to the server,
                        Client(serverIpAddress = ipAddressForServerSocket).connectToServer(this@MainActivity)
                    }
                }
            }
        })*/
    }

    fun wifiP2pState(isEnabled: Boolean) {
        mainActivityViewModel.wifiP2pStateChange(newState = isEnabled)
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

    override fun onResume() {
        super.onResume()
        // register the broadcast receiver
        registerReceiver(wifiDirectBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        // unregister the broadcast receiver
        unregisterReceiver(wifiDirectBroadcastReceiver)
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


