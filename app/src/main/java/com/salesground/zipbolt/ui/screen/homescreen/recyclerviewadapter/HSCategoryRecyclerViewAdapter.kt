package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

enum class HSCategoryRecyclerViewAdapterViewType(val viewType: Int) {
    APPLICATION(1),
    IMAGE(2),
    VIDEO(3),
    MUSIC(4)
}

class HSCategoryRecyclerViewAdapter
    : ListAdapter<DataCategory, RecyclerView.ViewHolder>(HSCategoryRecyclerViewDiffUtil) {

    override fun getItemViewType(position: Int): Int {
        return when(currentList[position]){
            is DataCategory.Application -> HSCategoryRecyclerViewAdapterViewType.APPLICATION.viewType
            is DataCategory.Image -> HSCategoryRecyclerViewAdapterViewType.IMAGE.viewType
            is DataCategory.Music -> HSCategoryRecyclerViewAdapterViewType.MUSIC.viewType
            is DataCategory.Video -> HSCategoryRecyclerViewAdapterViewType.VIDEO.viewType
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            HSCategoryRecyclerViewAdapterViewType.APPLICATION.viewType -> {
                // TODO, return ViewHolder for application

            }
            HSCategoryRecyclerViewAdapterViewType.IMAGE.viewType -> {
                // TODO, return ViewHolder for image
            }
            HSCategoryRecyclerViewAdapterViewType.VIDEO.viewType -> {
                // TODO, return ViewHolder for video
            }
            HSCategoryRecyclerViewAdapterViewType.MUSIC.viewType ->{
                // TODO, return ViewHolder for music
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}

object HSCategoryRecyclerViewDiffUtil : DiffUtil.ItemCallback<DataCategory>() {
    override fun areItemsTheSame(oldItem: DataCategory, newItem: DataCategory): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DataCategory, newItem: DataCategory): Boolean {
        return oldItem.itemId == newItem.itemId
    }

}
