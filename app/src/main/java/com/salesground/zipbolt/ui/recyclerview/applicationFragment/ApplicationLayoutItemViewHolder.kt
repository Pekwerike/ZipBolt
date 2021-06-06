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
import com.salesground.zipbolt.utils.transformDataSizeToMeasuredUnit

class ApplicationLayoutItemViewHolder(
    private val applicationLayoutItemBinding: ApplicationLayoutItemBinding
) : RecyclerView.ViewHolder(applicationLayoutItemBinding.root) {

    fun bindApplicationDetails(dataToTransfer: DataToTransfer) {
        dataToTransfer as DataToTransfer.DeviceApplication
        with(applicationLayoutItemBinding) {
            applicationName = dataToTransfer.applicationName
            applicationSizeFormattedAsString =
                dataToTransfer.appSize.transformDataSizeToMeasuredUnit()
            Glide.with(applicationIconImageView)
                .load(dataToTransfer.appIcon)
                .into(applicationIconImageView)
            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(parent: ViewGroup): ApplicationLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<ApplicationLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.application_layout_item,
                parent,
                false
            )
            return ApplicationLayoutItemViewHolder(layoutBinding)
        }
    }
}