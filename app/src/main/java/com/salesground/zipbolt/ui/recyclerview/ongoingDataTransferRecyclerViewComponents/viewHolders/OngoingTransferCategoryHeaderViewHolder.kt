package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.OngoingTransferCategoryHeaderBinding

class OngoingTransferCategoryHeaderViewHolder(
    ongoingTransferCategoryHeaderBinding: OngoingTransferCategoryHeaderBinding
) : RecyclerView.ViewHolder(ongoingTransferCategoryHeaderBinding.root) {


    companion object {
        fun createViewHolder(parent: ViewGroup): OngoingTransferCategoryHeaderViewHolder {
            val layoutBinding = OngoingTransferCategoryHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return OngoingTransferCategoryHeaderViewHolder(layoutBinding)
        }
    }
}