package com.salesground.zipbolt.ui.recyclerview.peersDiscoveryFragment

import android.net.wifi.p2p.WifiP2pDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

class PeersDiscoveredRecyclerViewAdapter(
    private val deviceClickListener: DataToTransferRecyclerViewItemClickListener<WifiP2pDevice>
) : ListAdapter<WifiP2pDevice,
        RecyclerView.ViewHolder>(PeersDiscoveredRecyclerViewAdapterDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiscoveredPeerLayoutItemViewHolder.createViewHolder(
            parent,
            deviceClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DiscoveredPeerLayoutItemViewHolder) {
            holder.bindData(getItem(position))
        }
    }
}

object PeersDiscoveredRecyclerViewAdapterDiffUtil : DiffUtil.ItemCallback<WifiP2pDevice>() {
    override fun areItemsTheSame(oldItem: WifiP2pDevice, newItem: WifiP2pDevice): Boolean {
        return oldItem.deviceAddress == newItem.deviceAddress
    }

    override fun areContentsTheSame(oldItem: WifiP2pDevice, newItem: WifiP2pDevice): Boolean {
        return oldItem == newItem
    }

}