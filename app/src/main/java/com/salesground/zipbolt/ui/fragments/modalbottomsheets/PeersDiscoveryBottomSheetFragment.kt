package com.salesground.zipbolt.ui.fragments.modalbottomsheets

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


class PeersDiscoveryBottomSheetFragment : BottomSheetDialogFragment() {
    private var shouldContinueDiscovery: Boolean = true
    private val peersDiscoveryViewModel by viewModels<PeersDiscoveryViewModel>()
    private var mainActivity: MainActivity? = null
    private lateinit var wifiP2pDnsSdServiceRequest: WifiP2pDnsSdServiceRequest

    private val wifiP2pManager: WifiP2pManager by lazy {
        context?.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }

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
        displayToast("Discovery started")
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
                endServiceDiscoveryInternal()
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
        shouldContinueDiscovery = false
        val wifiP2pConfig = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        wifiP2pManager.connect(wifiP2pChannel, wifiP2pConfig,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Broadcast receiver notifies us in WIFI_P2P_CONNECTION_CHANGED_ACTION
                    lifecycleScope.launch(Dispatchers.Main) {
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
                 //   displayToast("Connection attempt failed")
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
                                   // Log.i("SearchingForPeers", "Add service request successfully")
                                    discoverServices()
                                }
                            }

                            override fun onFailure(reason: Int) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                   // Log.i("SearchingForPeers", "Add service request failed")
                                  //  displayToast("Add service request failed")
                                    discoverServices()
                                }
                            }
                        })
                }

                override fun onFailure(reason: Int) {
                    lifecycleScope.launch(Dispatchers.Main) {
                      //  Log.i("SearchingForPeers", "Clear service failed")
                        // displayToast("Clear service failed")
                        beginServiceDiscovery()
                    }
                }
            })
    }

    private fun endServiceDiscoveryInternal() {
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

    fun endServiceDiscovery() {
        wifiP2pManager.removeServiceRequest(
            wifiP2pChannel,
            wifiP2pDnsSdServiceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {

                }

                override fun onFailure(reason: Int) {
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun discoverServices() {
        wifiP2pManager.discoverServices(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                if (shouldContinueDiscovery) {
                    Timer().schedule(2000) {
                        lifecycleScope.launch(Dispatchers.Main) {
                        //    Log.i("SearchingForPeers", "New discovery started")
                          //  displayToast("New discovery started")
                            discoverServices()
                        }
                    }
                }
            }

            override fun onFailure(reason: Int) {
                if (shouldContinueDiscovery) {
                    lifecycleScope.launch(Dispatchers.Main) {
                       // Log.i("SearchingForPeers", "Peers discovery failed")
                       // displayToast("Peers discovery failed")
                        discoverServices()
                    }
                }
            }
        })
    }

    private fun displayToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        fun newInstance(): PeersDiscoveryBottomSheetFragment {
            return PeersDiscoveryBottomSheetFragment()
        }
    }

}