package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ApplicationLayoutItemTransferOrReceiveBinding
import com.salesground.zipbolt.databinding.ApplicationReceiveCompleteLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.utils.transformDataSizeToMeasuredUnit

class ApplicationReceiveCompleteLayoutViewHolder(
    private val applicationReceiveCompleteLayoutItemBinding: ApplicationReceiveCompleteLayoutItemBinding
) : RecyclerView.ViewHolder(applicationReceiveCompleteLayoutItemBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceApplication

        applicationReceiveCompleteLayoutItemBinding.run {
            applicationName = dataToTransfer.dataDisplayName
            applicationSize = dataToTransfer.dataSize.transformDataSizeToMeasuredUnit()

            Glide.with(applicationReceiveLayoutItemImageView)
                .load(dataToTransfer.applicationIcon)
                .into(applicationReceiveLayoutItemImageView)
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ApplicationReceiveCompleteLayoutViewHolder {
            val layoutBinding =
                DataBindingUtil.inflate<ApplicationReceiveCompleteLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.application_receive_complete_layout_item,
                    parent,
                    false
                )
            return ApplicationReceiveCompleteLayoutViewHolder(layoutBinding)
        }
    }
}