package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.databinding.NoItemInTransferOrReceiveLayoutBinding

class NoItemInTransferOrReceiveViewHolder(
    private val noItemInTransferOrReceiveLayoutBinding:
    NoItemInTransferOrReceiveLayoutBinding
) : RecyclerView.ViewHolder(
    noItemInTransferOrReceiveLayoutBinding.root
) {

    companion object {
        fun createViewHolder(parent: ViewGroup): NoItemInTransferOrReceiveViewHolder {
            val layoutBinding = NoItemInTransferOrReceiveLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return NoItemInTransferOrReceiveViewHolder(layoutBinding)
        }
    }
}