package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.HomeScreenRecyclerviewLayoutItemBinding
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.HomeScreenRecyclerviewDataModel

enum class HomeScreenCategoryRecyclerViewAdapterViewType(val viewType: Int){

}
class HomeScreenCategoryRecyclerViewAdapter
    : ListAdapter<DataCategory, RecyclerView.ViewHolder>(HomeScreenCategoryRecyclerViewDiffUtil){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}

object HomeScreenCategoryRecyclerViewDiffUtil: DiffUtil.ItemCallback<DataCategory>(){
    override fun areItemsTheSame(oldItem: DataCategory, newItem: DataCategory): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DataCategory, newItem: DataCategory): Boolean {
        return oldItem.itemId == newItem.itemId
    }

}

class HomeScreenRecyclerViewHolder(
    private val
    homeScreenRecyclerviewLayoutItemBinding: HomeScreenRecyclerviewLayoutItemBinding
) : RecyclerView.ViewHolder(homeScreenRecyclerviewLayoutItemBinding.root) {
    fun bindData(homeScreenRecyclerviewDataModel: HomeScreenRecyclerviewDataModel) {
        homeScreenRecyclerviewLayoutItemBinding.categoryLabel =
            homeScreenRecyclerviewDataModel.dataCategory


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