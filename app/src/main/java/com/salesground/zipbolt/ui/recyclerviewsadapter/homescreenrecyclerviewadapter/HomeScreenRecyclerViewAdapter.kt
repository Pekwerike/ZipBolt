package com.salesground.zipbolt.ui.recyclerviewsadapter.homescreenrecyclerviewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.HomeScreenRecyclerviewLayoutItemBinding
import com.salesground.zipbolt.model.ApplicationModel

class HomeScreenRecyclerViewAdapter
    : ListAdapter<HomeScreenDataModel, RecyclerView.ViewHolder>(HomeScreenRecyclerViewDiffUtil()) {

    class HomeScreenRecyclerViewHolder(
        private val
        homeScreenRecyclerviewLayoutItemBinding: HomeScreenRecyclerviewLayoutItemBinding
    ) :
        RecyclerView.ViewHolder(homeScreenRecyclerviewLayoutItemBinding.root) {
        fun bindData(homeScreenDataModel: HomeScreenDataModel) {
            homeScreenRecyclerviewLayoutItemBinding.categoryLabel = homeScreenDataModel.homeScreenDataModelID
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

class HomeScreenRecyclerViewDiffUtil : DiffUtil.ItemCallback<HomeScreenDataModel>() {
    override fun areItemsTheSame(
        oldItem: HomeScreenDataModel,
        newItem: HomeScreenDataModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: HomeScreenDataModel,
        newItem: HomeScreenDataModel
    ): Boolean {
        return oldItem.homeScreenDataModelID == newItem.homeScreenDataModelID
    }

}

sealed class HomeScreenDataModel() {
    abstract val homeScreenDataModelID: String

    data class HomeScreenApplicationModel(
        val dataCategory: String,
        val applicationList: MutableList<ApplicationModel>
    ) : HomeScreenDataModel() {
        override val homeScreenDataModelID: String
            get() = dataCategory
    }

}