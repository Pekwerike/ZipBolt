package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.HomeScreenRecyclerviewDataModel
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder.HomeScreenRecyclerViewHolder


class HomeScreenRecyclerViewAdapter
    : ListAdapter<HomeScreenRecyclerviewDataModel, HomeScreenRecyclerViewHolder>(
    HomeScreenRecyclerViewDiffUtil
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeScreenRecyclerViewHolder {
        return HomeScreenRecyclerViewHolder.createHomeScreenRecyclerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: HomeScreenRecyclerViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

}

object HomeScreenRecyclerViewDiffUtil : DiffUtil.ItemCallback<HomeScreenRecyclerviewDataModel>() {
    override fun areItemsTheSame(
        oldItem: HomeScreenRecyclerviewDataModel,
        newItem: HomeScreenRecyclerviewDataModel
    ): Boolean {
        return oldItem.dataCategory == newItem.dataCategory
    }

    override fun areContentsTheSame(
        oldItem: HomeScreenRecyclerviewDataModel,
        newItem: HomeScreenRecyclerviewDataModel
    ): Boolean {
        return oldItem == newItem
    }
}


