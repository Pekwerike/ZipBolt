package com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewadapter.datamodel.DataCategory
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder.HSApplicationCategoryViewHolder
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder.HSAudioCategoryViewHolder
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder.HSImageCategoryViewHolder
import com.salesground.zipbolt.ui.screen.homescreen.recyclerviewholder.HSVideoCategoryViewHolder

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
      return when(viewType){
            HSCategoryRecyclerViewAdapterViewType.APPLICATION.viewType -> {
                HSApplicationCategoryViewHolder.createHSApplicationCategoryViewHolder(parent)

            }
            HSCategoryRecyclerViewAdapterViewType.IMAGE.viewType -> {
               HSImageCategoryViewHolder.createHSImageCategoryViewHolder(parent)
            }
            HSCategoryRecyclerViewAdapterViewType.VIDEO.viewType -> {
               HSVideoCategoryViewHolder.createHSVideoCategoryViewHolder(parent)
            }
            HSCategoryRecyclerViewAdapterViewType.MUSIC.viewType ->{
               HSAudioCategoryViewHolder.createHSAudioCategoryViewHolder(parent)
            }
          else -> HSApplicationCategoryViewHolder.createHSApplicationCategoryViewHolder(parent)
      }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is HSApplicationCategoryViewHolder -> holder.bindApplicationData(currentList[position] as DataCategory.Application)
            is HSImageCategoryViewHolder -> holder.bindImageDisplay(currentList[position] as DataCategory.Image)
            is HSVideoCategoryViewHolder -> holder.bindVideoData(currentList[position] as DataCategory.Video)
            is HSAudioCategoryViewHolder -> holder.bindAudioData(currentList[position] as DataCategory.Music)
        }
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
