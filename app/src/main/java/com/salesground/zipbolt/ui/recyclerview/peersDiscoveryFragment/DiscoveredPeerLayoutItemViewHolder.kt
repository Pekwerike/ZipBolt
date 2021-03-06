package com.salesground.zipbolt.ui.recyclerview.peersDiscoveryFragment

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.DiscoveredPeerLayoutItemBinding
import com.salesground.zipbolt.ui.recyclerview.RecyclerViewItemClickedListener

class DiscoveredPeerLayoutItemViewHolder(
    private val discoveredPeerLayoutItemBinding: DiscoveredPeerLayoutItemBinding,
    private val deviceClickedListener: RecyclerViewItemClickedListener<WifiP2pDevice>
) : RecyclerView.ViewHolder(discoveredPeerLayoutItemBinding.root) {

    fun bindData(device: WifiP2pDevice) {
        discoveredPeerLayoutItemBinding.run {
            deviceName = if (device.deviceName.isNotBlank()) {
                device.deviceName
            } else {
                "Unspecified Device"
            }
            discoveredPeerLayoutItemViewGroup.setOnClickListener {
                deviceClickedListener.onClick(device)
            }

            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            deviceClickedListener: RecyclerViewItemClickedListener<WifiP2pDevice>
        ): DiscoveredPeerLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<
                    DiscoveredPeerLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.discovered_peer_layout_item,
                parent,
                false
            )
            return DiscoveredPeerLayoutItemViewHolder(
                layoutBinding,
                deviceClickedListener
            )
        }
    }
}