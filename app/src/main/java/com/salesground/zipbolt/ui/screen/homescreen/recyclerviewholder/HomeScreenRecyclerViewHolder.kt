package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.HomeScreenRecyclerviewLayoutItemBinding
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HSCategoryRecyclerViewAdapter
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel

class HomeScreenRecyclerViewHolder(
    private val
    homeScreenRecyclerviewLayoutItemBinding: HomeScreenRecyclerviewLayoutItemBinding
) : RecyclerView.ViewHolder(homeScreenRecyclerviewLayoutItemBinding.root) {

    fun bindData(homeScreenRecyclerviewDataModel: HomeScreenRecyclerviewDataModel) {
        homeScreenRecyclerviewLayoutItemBinding.categoryLabel =
            homeScreenRecyclerviewDataModel.dataCategory
        homeScreenRecyclerviewLayoutItemBinding.categoryItemsRecyclerview

        val hSRCAdapter = HSCategoryRecyclerViewAdapter()
        hSRCAdapter.submitList(homeScreenRecyclerviewDataModel.mediaCollection)
        homeScreenRecyclerviewLayoutItemBinding.categoryItemsRecyclerview.adapter = hSRCAdapter
        val hSRCLayoutManager = when (homeScreenRecyclerviewDataModel.dataCategory) {
            "Apps" -> GridLayoutManager(homeScreenRecyclerviewLayoutItemBinding.root.context, 5)
            "Images" -> GridLayoutManager(homeScreenRecyclerviewLayoutItemBinding.root.context, 4)
            else -> GridLayoutManager(homeScreenRecyclerviewLayoutItemBinding.root.context, 4)
        }

        homeScreenRecyclerviewLayoutItemBinding.categoryItemsRecyclerview.layoutManager =
            hSRCLayoutManager
    }

    companion object {
        fun createHomeScreenRecyclerViewHolder(parent: ViewGroup): HomeScreenRecyclerViewHolder {
            val homeScreenRecyclerViewLayoutItemBinding =
                DataBindingUtil.inflate<HomeScreenRecyclerviewLayoutItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.home_screen_recyclerview_layout_item,
                    parent,
                    false
                )
            return HomeScreenRecyclerViewHolder(homeScreenRecyclerViewLayoutItemBinding)
        }
    }
}