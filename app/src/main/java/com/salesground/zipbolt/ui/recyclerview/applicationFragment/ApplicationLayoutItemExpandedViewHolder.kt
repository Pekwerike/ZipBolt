package com.salesground.zipbolt.ui.recyclerview.applicationFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ApplicationLayoutItemLongAppNameBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.utils.transformDataSizeToMeasuredUnit

class ApplicationLayoutItemExpandedViewHolder(
    private val applicationLayoutItemLongAppNameBinding: ApplicationLayoutItemLongAppNameBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
) : RecyclerView.ViewHolder(applicationLayoutItemLongAppNameBinding.root) {

    fun bindAppData(
        dataToTransfer: DataToTransfer,
        selectedApplications: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceApplication

        applicationLayoutItemLongAppNameBinding.run {
            applicationName = dataToTransfer.applicationName
            applicationSizeFormattedAsString =
                dataToTransfer.appSize.transformDataSizeToMeasuredUnit()
            Glide.with(applicationLayoutItemLongAppNameAppIconImageView)
                .load(dataToTransfer.appIcon)
                .into(applicationLayoutItemLongAppNameAppIconImageView)

            applicationLayoutItemLongAppNameSelectableConstraintLayout.run {
                setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedApplications.contains(dataToTransfer)) {
                        setIsViewSelected(false)
                        // user un-selected, so remove the application from the collection
                        selectedApplications.remove(dataToTransfer)
                    } else {
                        setIsViewSelected(true)
                        // user selects, so add the application to the collection of clicked application
                        selectedApplications.add(dataToTransfer)
                    }
                }
                if (selectedApplications.contains(dataToTransfer)) {
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
        ): ApplicationLayoutItemExpandedViewHolder {
            val layoutBinding = DataBindingUtil.inflate<ApplicationLayoutItemLongAppNameBinding>(
                LayoutInflater.from(parent.context),
                R.layout.application_layout_item_long_app_name,
                parent,
                false
            )
            return ApplicationLayoutItemExpandedViewHolder(
                layoutBinding,
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}