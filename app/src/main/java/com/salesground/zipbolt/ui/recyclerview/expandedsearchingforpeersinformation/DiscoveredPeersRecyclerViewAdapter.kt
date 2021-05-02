package com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation

import android.net.wifi.p2p.WifiP2pDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.ui.DiscoveredPeersDataItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class DiscoveredPeersDataItemEnum(val category: Int) {
    HEADER(1),
    DISCOVERED_PEER(2)
}

class DiscoveredPeersRecyclerViewAdapter(
    private val connectToDeviceClickListener: ConnectToDeviceClickListener
) :
    ListAdapter<DiscoveredPeersDataItem, RecyclerView.ViewHolder>(
        DiscoveredPeersRecyclerViewAdapterDiffUitl
    ) {

    interface ConnectToDeviceClickListener {
        fun onConnectToDevice(wifiP2pDevice: WifiP2pDevice)
    }

    override fun submitList(list: MutableList<DiscoveredPeersDataItem>?) {
        CoroutineScope(Dispatchers.IO).launch {
            val newList =
                if (list.isNullOrEmpty()) listOf<DiscoveredPeersDataItem>(DiscoveredPeersDataItem.Header)
                else {
                    listOf(DiscoveredPeersDataItem.Header) + list
                }
            withContext(Dispatchers.Main) {
                super.submitList(newList)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> DiscoveredPeersDataItemEnum.HEADER.category
            else -> DiscoveredPeersDataItemEnum.DISCOVERED_PEER.category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DiscoveredPeersDataItemEnum.HEADER.category -> {
                DiscoveredPeersHeaderViewHolder.createDiscoveredPeersHeaderViewHolder(parent)
            }
            else -> {
                DiscoveredPeerViewHolder.createDiscoveredPeerViewHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoveredPeerViewHolder -> {
                holder.bindDeviceData(
                    (getItem(position) as DiscoveredPeersDataItem.DiscoveredPeer).wifiP2pDevice,
                    connectToDeviceClickListener
                )
            }
        }
    }

    object DiscoveredPeersRecyclerViewAdapterDiffUitl :
        DiffUtil.ItemCallback<DiscoveredPeersDataItem>() {
        override fun areItemsTheSame(
            oldItem: DiscoveredPeersDataItem,
            newItem: DiscoveredPeersDataItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DiscoveredPeersDataItem,
            newItem: DiscoveredPeersDataItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}

