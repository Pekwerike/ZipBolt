package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ApplicationLayoutItemBinding
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory

class HSApplicationCategoryViewHolder(
    private val applicationLayoutItemBinding:
    ApplicationLayoutItemBinding
) : RecyclerView.ViewHolder(applicationLayoutItemBinding.root) {

    fun bindApplicationData(dataCategory: DataCategory.Application){
        applicationLayoutItemBinding.applicationLabel = dataCategory.applicationModel.applicationName
        Glide.with(applicationLayoutItemBinding.applicationIconImageView.context)
            .load(dataCategory.applicationModel.appIcon)
            .override(150)
            .into(applicationLayoutItemBinding.applicationIconImageView)

    }

    companion object {
        fun createHSApplicationCategoryViewHolder(parent: ViewGroup)
                : HSApplicationCategoryViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<ApplicationLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.application_layout_item,
                parent,
                false
            )
            return HSApplicationCategoryViewHolder(layoutItemBinding)
        }
    }
}