package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ApplicationLayoutItemTransferOrReceiveBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.utils.transformDataSizeToMeasuredUnit

class ApplicationReceiveCompleteLayoutViewHolder(
    private val applicationLayoutItemTransferOrReceiveBinding: ApplicationLayoutItemTransferOrReceiveBinding
) : RecyclerView.ViewHolder(applicationLayoutItemTransferOrReceiveBinding.root) {

    fun bindData(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceApplication

        applicationLayoutItemTransferOrReceiveBinding.run {
            applicationName = dataToTransfer.dataDisplayName
            applicationSize = dataToTransfer.dataSize.transformDataSizeToMeasuredUnit()

            Glide.with(applicationLayoutItemTransferOrReceiveImageView)
                .load(dataToTransfer.applicationIcon)
                .into(applicationLayoutItemTransferOrReceiveImageView)

            applicationLayoutItemTransferOrReceiveShimmer.run {
                stopShimmer()
                hideShimmer()
            }

            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ApplicationReceiveCompleteLayoutViewHolder {
            val layoutBinding =
                DataBindingUtil.inflate<ApplicationLayoutItemTransferOrReceiveBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.application_layout_item_transfer_or_receive,
                    parent,
                    false
                )
            return ApplicationReceiveCompleteLayoutViewHolder(layoutBinding)
        }
    }
}