package com.salesground.zipbolt.ui.recyclerview.applicationFragment

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
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
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
) : RecyclerView.ViewHolder(applicationLayoutItemBinding.root) {

    fun bindApplicationDetails(
        dataToTransfer: DataToTransfer,
        isApplicationItemSelected: Boolean
    ) {
        dataToTransfer as DataToTransfer.DeviceApplication
        with(applicationLayoutItemBinding) {
            applicationName = if (dataToTransfer.applicationName?.contains("Google", true) == true
                && dataToTransfer.applicationName.trim().length != 6
            ) {
                dataToTransfer.applicationName.subSequence(7, dataToTransfer.applicationName.length)
                    .trim().toString()
            } else {
                dataToTransfer.applicationName
            }
            applicationSizeFormattedAsString =
                dataToTransfer.appSize.transformDataSizeToMeasuredUnit()
            Glide.with(applicationIconImageView)
                .load(dataToTransfer.appIcon)
                .into(applicationIconImageView)
            executePendingBindings()
            with(applicationLayoutItemSelectableLinearlayout) {
                setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )
                    setIsViewSelected(true)
                }
                if (isApplicationItemSelected) {
                    setIsViewSelected(true)
                } else {
                    setIsViewSelected(false)
                }
            }
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
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