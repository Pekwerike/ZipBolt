package com.salesground.zipbolt.ui.fragments.modalbottomsheets

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentPeersDiscoveryBottomSheetBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.peersDiscoveryFragment.PeersDiscoveredRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.PeersDiscoveryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


@AndroidEntryPoint
class PeersDiscoveryBottomSheetFragment : BottomSheetDialogFragment() {
    private val peersDiscoveryViewModel by activityViewModels<PeersDiscoveryViewModel>()
    private var mainActivity: MainActivity? = null
    private lateinit var wifiP2pDnsSdServiceRequest: WifiP2pDnsSdServiceRequest

    @Inject
    lateinit var wifiManager: WifiManager

    @Inject
    lateinit var wifiP2pManager: WifiP2pManager

    private lateinit var wifiP2pChannel: WifiP2pManager.Channel
    private lateinit var peersDiscoveryBottomSheetFragment: FragmentPeersDiscoveryBottomSheetBinding
    private val peersDiscoveredRecyclerViewAdapter = PeersDiscoveredRecyclerViewAdapter(
        DataToTransferRecyclerViewItemClickListener {
            if (peersDiscoveryBottomSheetFragment.fragmentPeersDiscoveryDiscoveredPeersRecyclerView.visibility != View.INVISIBLE) {
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
        beginServiceDiscovery()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        peersDiscoveryBottomSheetFragment = FragmentPeersDiscoveryBottomSheetBinding.inflate(
            inflater, container, false
        )
        // Inflate the layout for this fragment
        return peersDiscoveryBottomSheetFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        peersDiscoveryBottomSheetFragment.run {
            fragmentPeersDiscoveryDiscoveredPeersRecyclerView.run {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = peersDiscoveredRecyclerViewAdapter
            }
            fragmentPeersDiscoveryStopDiscoveryImageButton.setOnClickListener {
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
                        ""
                        peersDiscoveryBottomSheetFragment.run {
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
                            fragmentPeersDiscoverySearchingForPeersTextView.visibility =
                                View.INVISIBLE
                            fragmentPeersDiscoveryDiscoveredPeersRecyclerView.visibility =
                                View.INVISIBLE
                            fragmentPeersDiscoveryDiscoveredPeersTextView.visibility =
                                View.INVISIBLE
                            fragmentPeersDiscoverySearchingForSenderDescriptionTextView.visibility =
                                View.INVISIBLE
                        }
                    }
                }

                override fun onFailure(p0: Int) {
                    // connection initiation failed,
                  //  displayToast("Connection attempt failed")
                }
            })
    }


    @SuppressLint("MissingPermission")
    private fun beginServiceDiscovery() {
        val recordListener =
            WifiP2pManager.DnsSdTxtRecordListener { fullDomainName: String?,
                                                    txtRecordMap: MutableMap<String, String>?, srcDevice: WifiP2pDevice? ->
            }

        val serviceInfoListener =
            WifiP2pManager.DnsSdServiceResponseListener { instanceName: String?,
                                                          registrationType: String?,
                                                          srcDevice: WifiP2pDevice? ->
                srcDevice?.let {
                    if (instanceName == getString(R.string.zip_bolt_file_transfer_service))
                        peersDiscoveryViewModel.addDiscoveredDevice(srcDevice)
                }
            }

        wifiP2pManager.setDnsSdResponseListeners(
            wifiP2pChannel,
            serviceInfoListener,
            recordListener
        )
        wifiP2pDnsSdServiceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        wifiP2pManager.removeServiceRequest(wifiP2pChannel,
            wifiP2pDnsSdServiceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    wifiP2pManager.addServiceRequest(wifiP2pChannel,
                        wifiP2pDnsSdServiceRequest,
                        object : WifiP2pManager.ActionListener {
                            override fun onSuccess() {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    // displayToast("Add service request succeeded")
                                    discoverServices()
                                }
                            }

                            override fun onFailure(reason: Int) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                  //  displayToast("Add service request failed")
                                    discoverServices()
                                }
                            }
                        })
                }

                override fun onFailure(reason: Int) {
                    lifecycleScope.launch(Dispatchers.Main) {
                       // displayToast("Clear service failed")
                        beginServiceDiscovery()
                    }
                }
            })
    }

    private fun endServiceDiscovery() {
        wifiP2pManager.removeServiceRequest(
            wifiP2pChannel,
            wifiP2pDnsSdServiceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    mainActivity?.closePeersDiscoveryModalBottomSheet()
                }

                override fun onFailure(reason: Int) {
                    mainActivity?.closePeersDiscoveryModalBottomSheet()
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun discoverServices() {
        wifiP2pManager.discoverServices(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Timer().schedule(2000) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        //   displayToast(" new service discovery started")
                        discoverServices()
                    }
                }
            }

            override fun onFailure(reason: Int) {
                lifecycleScope.launch(Dispatchers.Main) {
                    //displayToast("Peers discovery failed")
                    discoverServices()
                }
            }
        })
    }

    private fun displayToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun killSelf() {
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        peersDiscoveryViewModel.clearDiscoveredPeerSet()
    }

    companion object {
        fun newInstance(): PeersDiscoveryBottomSheetFragment {
            return PeersDiscoveryBottomSheetFragment()
        }
    }

}