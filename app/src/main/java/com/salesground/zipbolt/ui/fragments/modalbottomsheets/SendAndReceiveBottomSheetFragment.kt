package com.salesground.zipbolt.ui.fragments.modalbottomsheets

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentSendAndReceiveBottomSheetBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.peersDiscoveryFragment.PeersDiscoveredRecyclerViewAdapter
import com.salesground.zipbolt.utils.ConnectionUtils
import com.salesground.zipbolt.viewmodel.PeersDiscoveryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@AndroidEntryPoint
class SendAndReceiveBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var fragmentSendAndReceiveBottomSheetBinding: FragmentSendAndReceiveBottomSheetBinding
    private val peersDiscoveryViewModel by viewModels<PeersDiscoveryViewModel>()
    private var mainActivity: MainActivity? = null

    @Inject
    lateinit var wifiManager: WifiManager

    @Inject
    lateinit var wifiP2pManager: WifiP2pManager

    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private val peersDiscoveredRecyclerViewAdapter = PeersDiscoveredRecyclerViewAdapter(
        DataToTransferRecyclerViewItemClickListener {
            if (fragmentSendAndReceiveBottomSheetBinding.fragmentSendAndReceiveDiscoveredPeersRecyclerView.visibility != View.INVISIBLE) {
                connectToADevice(it)
            }
        }
    )

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

        observeViewModelLiveData()
        removeAllLocalServices()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSendAndReceiveBottomSheetBinding = FragmentSendAndReceiveBottomSheetBinding.inflate(
            layoutInflater,
            container,
            false
        )

        return fragmentSendAndReceiveBottomSheetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentSendAndReceiveBottomSheetBinding.run {
            fragmentSendAndReceiveDiscoveredPeersRecyclerView.run {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = peersDiscoveredRecyclerViewAdapter
            }
            fragmentSendAndReceiveStopPeerDiscoveryImageButton.setOnClickListener {
                endServiceDiscovery()
            }
        }
    }

    private fun observeViewModelLiveData() {
        peersDiscoveryViewModel.discoveredPeerSet.observe(this) {
            it?.let {
                peersDiscoveredRecyclerViewAdapter.submitList(it.toList())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun removeAllLocalServices() {
        wifiP2pManager.clearLocalServices(wifiP2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    addLocalService()
                }

                override fun onFailure(reason: Int) {
                    removeAllLocalServices()
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun addLocalService() {
        val record: Map<String, String> = mapOf(
            ConnectionUtils.TRANSFER_TYPE_EXTRA to ConnectionUtils.TRANSFER_TYPE_SEND_AND_RECEIVE
        )

        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            getString(R.string.zip_bolt_file_transfer_service),
            ConnectionUtils.ZIP_BOLT_TRANSFER_SERVICE,
            record
        )
        wifiP2pManager.addLocalService(
            wifiP2pChannel,
            serviceInfo,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    clearServiceRequests()
                }

                override fun onFailure(reason: Int) {
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun connectToADevice(device: WifiP2pDevice) {
        val wifiP2pConfig = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        wifiP2pManager.connect(wifiP2pChannel, wifiP2pConfig,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Broadcast receiver notifies us in WIFI_P2P_CONNECTION_CHANGED_ACTION
                    lifecycleScope.launch(Dispatchers.Main) {
                        fragmentSendAndReceiveBottomSheetBinding.run {
                            fragmentPeersDiscoveryConnectingToPeerTextView.run {
                                setAnimatedText(
                                    getString(
                                        R.string.connecting_to_device_message_place_holder,
                                        device.deviceName
                                    )
                                )
                                fragmentPeersDiscoveryConnectingToPeerTextView.visibility =
                                    View.VISIBLE
                            }
                            fragmentSendAndReceiveSearchingForPeersTextView.visibility =
                                View.INVISIBLE
                            fragmentSendAndReceiveDiscoveredPeersRecyclerView.visibility =
                                View.INVISIBLE
                            fragmentSendAndReceiveDiscoveredPeersTextView.visibility =
                                View.INVISIBLE
                            fragmentPeersDiscoverySearchingForSenderDescriptionTextView.visibility =
                                View.INVISIBLE
                        }
                    }
                }

                override fun onFailure(p0: Int) {
                    // connection initiation failed,
                }
            })
    }

    private fun clearServiceRequests() {
        wifiP2pManager.clearServiceRequests(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                addServiceRequests()
            }

            override fun onFailure(reason: Int) {
                addServiceRequests()
            }
        })
    }

    private fun addServiceRequests() {
        val recordListener =
            WifiP2pManager.DnsSdTxtRecordListener { fullDomainName, txtRecordMap, srcDevice ->
                peersDiscoveryViewModel.deviceTransferTypeMap.put(
                    srcDevice.deviceAddress,
                    txtRecordMap[ConnectionUtils.TRANSFER_TYPE_EXTRA]
                        ?: ConnectionUtils.TRANSFER_TYPE_SEND
                )
            }
        val serviceListener =
            WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, srcDevice ->
                if (instanceName == getString(R.string.zip_bolt_file_transfer_service)) {
                    val transferType =
                        peersDiscoveryViewModel.deviceTransferTypeMap[srcDevice.deviceAddress]
                    if (transferType == ConnectionUtils.TRANSFER_TYPE_SEND_AND_RECEIVE) {
                        // display this device to the user
                        peersDiscoveryViewModel.addDiscoveredDevice(srcDevice)
                    }
                }
            }
        wifiP2pManager.setDnsSdResponseListeners(wifiP2pChannel, serviceListener, recordListener)
        wifiP2pManager.addServiceRequest(wifiP2pChannel,
            WifiP2pDnsSdServiceRequest.newInstance(),
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    discoverServices()
                }

                override fun onFailure(reason: Int) {
                    discoverServices()
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun discoverServices() {
        wifiP2pManager.discoverServices(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Timer().schedule(2000) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        discoverServices()
                    }
                }
            }

            override fun onFailure(reason: Int) {
                if (reason == WifiP2pManager.ERROR || reason == WifiP2pManager.BUSY) {
                    discoverServices()
                }
            }
        })
    }

    private fun endServiceDiscovery() {
        wifiP2pManager.removeServiceRequest(
            wifiP2pChannel,
            WifiP2pDnsSdServiceRequest.newInstance(),
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    mainActivity?.closeSendAndReceiveModalBottomSheet()
                }

                override fun onFailure(reason: Int) {
                    mainActivity?.closeSendAndReceiveModalBottomSheet()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
       // displayToast("Destroyed")
        peersDiscoveryViewModel.clearDiscoveredPeerSet()
    }

    private fun displayToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = SendAndReceiveBottomSheetFragment()
    }
}