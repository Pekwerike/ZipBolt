package com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.WifiP2pDeviceItemLayoutBinding

class DiscoveredPeerViewHolder(
    private val wifiP2pDeviceItemLayoutBinding:
    WifiP2pDeviceItemLayoutBinding
) : RecyclerView.ViewHolder(wifiP2pDeviceItemLayoutBinding.root) {

    fun bindDeviceData(device: WifiP2pDevice,
    connectToDeviceClickListener: DiscoveredPeersRecyclerViewAdapter.ConnectToDeviceClickListener) {
        wifiP2pDeviceItemLayoutBinding.apply {
            deviceName = device.deviceName ?: "Unknow device"

            // TODO add on click listener on the connect button
            wifiP2pDeviceItemLayoutConnectToDeviceButton.setOnClickListener {
                connectToDeviceClickListener.onConnectToDevice(wifiP2pDevice = device)
            }
        }
    }

    companion object{
        fun createDiscoveredPeerViewHolder(parent: ViewGroup) : DiscoveredPeerViewHolder {
            val layoutBinding = DataBindingUtil.inflate<WifiP2pDeviceItemLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.wifi_p2p_device_item_layout,
                parent,
                false
            )
            return DiscoveredPeerViewHolder(layoutBinding)
        }
    }
}