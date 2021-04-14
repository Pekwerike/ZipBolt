package com.salesground.zipbolt.ui.recyclerview.expandedsearchingforpeersinformation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.databinding.DiscoveredPeerRecyclerviewItemHeaderBinding

class DiscoveredPeersHeaderViewHolder(discoveredPeerRecyclerviewItemHeaderBinding:
                                      DiscoveredPeerRecyclerviewItemHeaderBinding) :
    RecyclerView.ViewHolder(discoveredPeerRecyclerviewItemHeaderBinding.root) {

        companion object{
            fun createDiscoveredPeersHeaderViewHolder(parent: ViewGroup) : DiscoveredPeersHeaderViewHolder {
                val layoutItemBinding = DiscoveredPeerRecyclerviewItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return DiscoveredPeersHeaderViewHolder(layoutItemBinding)
            }
        }
}