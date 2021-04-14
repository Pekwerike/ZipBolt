package com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation

import android.net.wifi.p2p.WifiP2pDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class DiscoveredPeersRecyclerViewAdapter(
    private val connectToDeviceClickListener : ConnectToDeviceClickListener
) :
    ListAdapter<DiscoveredPeersDataItem, RecyclerView.ViewHolder>(DiscoveredPeersRecyclerViewAdapterDiffUitl) {

    interface ConnectToDeviceClickListener{
        fun onConnectToDevice(wifiP2pDevice: WifiP2pDevice)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiscoveredPeerViewHolder.createDiscoveredPeerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    object DiscoveredPeersRecyclerViewAdapterDiffUitl : DiffUtil.ItemCallback<DiscoveredPeersDataItem>() {
        override fun areItemsTheSame(oldItem: DiscoveredPeersDataItem, newItem: DiscoveredPeersDataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DiscoveredPeersDataItem, newItem: DiscoveredPeersDataItem): Boolean {
            return oldItem == newItem
        }
    }
}

sealed class DiscoveredPeersDataItem(val id : String){
    object Header : DiscoveredPeersDataItem(id = "discoveredPeersDataItemHeader")
    class  DiscoveredPeer(wifiP2pDevice: WifiP2pDevice) : DiscoveredPeersDataItem(id = wifiP2pDevice.deviceName)
}