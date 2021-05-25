package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.OngoingTransferCategoryHeaderBinding

class OngoingTransferCategoryHeaderViewHolder(
    private val ongoingTransferCategoryHeaderBinding: OngoingTransferCategoryHeaderBinding
) : RecyclerView.ViewHolder(ongoingTransferCategoryHeaderBinding.root) {

    fun bindCategoryHeaderTitle(headerTitle: String) {
        ongoingTransferCategoryHeaderBinding.apply {
            this.headerTitle = headerTitle
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): OngoingTransferCategoryHeaderViewHolder {
            val layoutBinding = DataBindingUtil.inflate<OngoingTransferCategoryHeaderBinding>(
                LayoutInflater.from(parent.context),
                R.layout.ongoing_transfer_category_header,
                parent,
                false
            )
            return OngoingTransferCategoryHeaderViewHolder(layoutBinding)
        }
    }
}