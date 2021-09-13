package com.salesground.zipbolt.ui.fragments.modalbottomsheets

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentGroupCreatedBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class will only be open given that
 * 1. Wifi is enabled
 * 2. ZipBolt has been given access to device location**/
class GroupCreatedBottomSheetFragment : BottomSheetDialogFragment() {
    private val wifiP2pManager: WifiP2pManager by lazy{
        context?.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var fragmentGroupCreatedBinding: FragmentGroupCreatedBottomSheetBinding
    private var mainActivity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            mainActivity = it as MainActivity
        }
        // initialize wifi p2p channel
        wifiP2pChannel = wifiP2pManager.initialize(
            requireContext(), Looper.getMainLooper()
        ) {

        }
        createWifiDirectGroup()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentGroupCreatedBinding = FragmentGroupCreatedBottomSheetBinding.inflate(
            inflater, container, false
        )
        return fragmentGroupCreatedBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentGroupCreatedBinding.run {
            fragmentGroupCreatedCloseGroupImageButton.setOnClickListener {
                stopAdvertisingServiceToNearbyDevices()
            }
        }
    }

    private fun stopAdvertisingServiceToNearbyDevices() {
        wifiP2pManager.clearLocalServices(wifiP2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    closeWifiDirectGroup()
                }

                override fun onFailure(reason: Int) {
                    closeWifiDirectGroup()
                }
            })
    }

    private fun closeWifiDirectGroup() {
        wifiP2pManager.removeGroup(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                lifecycleScope.launch(Dispatchers.Main) {
                    mainActivity?.closeGroupCreatedModalBottomSheet()
                }
            }

            override fun onFailure(reason: Int) {
                lifecycleScope.launch(Dispatchers.Main) {
                    mainActivity?.closeGroupCreatedModalBottomSheet()
                }
            }
        })
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun createWifiDirectGroup() {
        // local service addition was successfully sent to the android framework
        wifiP2pManager.createGroup(wifiP2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    lifecycleScope.launch(Dispatchers.Main) {
                        broadcastZipBoltFileTransferService()
                    }
                }

                override fun onFailure(p0: Int) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        broadcastZipBoltFileTransferService()
                    }
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun broadcastZipBoltFileTransferService() {
        /* val listeningPort : Int = DataTransferService.arrayOfPossiblePorts.random()
          DataTransferService.SOCKET_PORT = listeningPort */
        // register the zipBolt file transfer service
        val record: Map<String, String> = mapOf(
            "peerName" to "",
            //  "listeningPort" to listeningPort.toString()
        )

        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            getString(R.string.zip_bolt_file_transfer_service),
            "_presence._tcp",
            record
        )

        wifiP2pManager.clearLocalServices(
            wifiP2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    wifiP2pManager.addLocalService(wifiP2pChannel, serviceInfo,
                        object : WifiP2pManager.ActionListener {
                            override fun onSuccess() {
                            }

                            override fun onFailure(reason: Int) {
                                // local service addition was not successfully sent to the android framework
                                lifecycleScope.launch(Dispatchers.Main) {
                                    broadcastZipBoltFileTransferService()
                                }
                            }
                        })
                }

                override fun onFailure(reason: Int) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        broadcastZipBoltFileTransferService()
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
       // Toast.makeText(requireContext(), "Resumed", Toast.LENGTH_SHORT).show()
    }
    companion object {
        fun newInstance(): GroupCreatedBottomSheetFragment {
            return GroupCreatedBottomSheetFragment()
        }
    }
}