package com.salesground.zipbolt.ui.fragments.modalbottomsheets

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentSendAndReceiveBottomSheetBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.peersDiscoveryFragment.PeersDiscoveredRecyclerViewAdapter
import com.salesground.zipbolt.utils.ConnectionUtils
import com.salesground.zipbolt.viewmodel.PeersDiscoveryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


class SendAndReceiveBottomSheetFragment : Fragment() {
    private lateinit var fragmentSendAndReceiveBottomSheetBinding: FragmentSendAndReceiveBottomSheetBinding
    private val peersDiscoveryViewModel by activityViewModels<PeersDiscoveryViewModel>()
    private var mainAction: MainActivity? = null

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
        clearServiceRequests()
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

        }
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
                        if (isVisible) {
                            discoverServices()
                            // displayToast("Searching for peers")
                        }
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

    companion object {
        fun newInstance() = SendAndReceiveBottomSheetFragment()
    }
}