package com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation

import android.net.wifi.p2p.WifiP2pDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class DiscoveredPeersRecyclerViewAdapter(
    private val connectToDeviceClickListener : ConnectToDeviceClickListener
) :
    ListAdapter<WifiP2pDevice, DiscoveredPeerViewHolder>(DiscoveredPeersRecyclerViewAdapterDiffUitl) {

    interface ConnectToDeviceClickListener{
        fun onConnectToDevice(wifiP2pDevice: WifiP2pDevice)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveredPeerViewHolder {
        return DiscoveredPeerViewHolder.createDiscoveredPeerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DiscoveredPeerViewHolder, position: Int) {
        holder.bindDeviceData(getItem(position), connectToDeviceClickListener)
    }

    object DiscoveredPeersRecyclerViewAdapterDiffUitl : DiffUtil.ItemCallback<WifiP2pDevice>() {
        override fun areItemsTheSame(oldItem: WifiP2pDevice, newItem: WifiP2pDevice): Boolean {
            return oldItem.deviceName == newItem.deviceName
        }

        override fun areContentsTheSame(oldItem: WifiP2pDevice, newItem: WifiP2pDevice): Boolean {
            return oldItem == newItem
        }
    }
}