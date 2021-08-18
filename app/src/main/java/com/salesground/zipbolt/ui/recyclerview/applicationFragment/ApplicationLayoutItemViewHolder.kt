package com.salesground.zipbolt.ui.recyclerview.applicationFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ApplicationLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.utils.transformDataSizeToMeasuredUnit

class ApplicationLayoutItemViewHolder(
    private val applicationLayoutItemBinding: ApplicationLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
) : RecyclerView.ViewHolder(applicationLayoutItemBinding.root) {

    fun bindApplicationDetails(
        dataToTransfer: DataToTransfer,
        selectedApplications: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceApplication

        with(applicationLayoutItemBinding) {
            applicationName = dataToTransfer.applicationName
            applicationSizeFormattedAsString =
                dataToTransfer.appSize.transformDataSizeToMeasuredUnit()

            Glide.with(applicationIconImageView)
                .load(dataToTransfer.applicationIcon)
                .into(applicationIconImageView)

            with(applicationLayoutItemSelectableLinearlayout) {
                setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedApplications.contains(dataToTransfer)) {
                        setIsViewSelected(true)
                    } else {
                        setIsViewSelected(false)
                    }
                }
                if (selectedApplications.contains(dataToTransfer)) {
                    setIsViewSelected(true)
                } else {
                    setIsViewSelected(false)
                }
            }
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
        ): ApplicationLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<ApplicationLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.application_layout_item,
                parent,
                false
            )
            return ApplicationLayoutItemViewHolder(
                layoutBinding,
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}