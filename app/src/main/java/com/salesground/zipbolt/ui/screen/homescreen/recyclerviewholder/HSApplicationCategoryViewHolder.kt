package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.ApplicationLayoutItemBinding
import com.salesground.zipbolt.model.ApplicationModel

class HSApplicationCategoryViewHolder(
    private val applicationLayoutItemBinding:
    ApplicationLayoutItemBinding
) : RecyclerView.ViewHolder(applicationLayoutItemBinding.root) {

    fun bindApplicationData(data : ApplicationModel){
        applicationLayoutItemBinding.applicationLabel = data.applicationName
        Glide.with(applicationLayoutItemBinding.applicationIconImageView.context)
            .load(data.appIcon)
            .override(50)
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