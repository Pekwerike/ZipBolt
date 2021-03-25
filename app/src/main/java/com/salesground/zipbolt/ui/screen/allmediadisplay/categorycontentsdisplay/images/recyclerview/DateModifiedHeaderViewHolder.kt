package com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.MediaDateModifiedHeaderBinding
import com.salesground.zipbolt.ui.screen.allmediadisplay.categorycontentsdisplay.images.dto.ImagesDisplayModel

class DateModifiedHeaderViewHolder(
    private val mediaDateModifiedHeaderBinding:
    MediaDateModifiedHeaderBinding
) : RecyclerView.ViewHolder(mediaDateModifiedHeaderBinding.root) {

    fun bindDateModified(dateModified: ImagesDisplayModel.ImagesDateModifiedHeader) {
      mediaDateModifiedHeaderBinding.apply {
          this.dateModified = dateModified.dateModified
          executePendingBindings()
      }
    }

    companion object {
        fun createDateModifiedHeaderViewHolder(parent: ViewGroup): DateModifiedHeaderViewHolder {
            val layoutItemBinding = DataBindingUtil.inflate<MediaDateModifiedHeaderBinding>(
                LayoutInflater.from(parent.context),
                R.layout.media_date_modified_header,
                parent,
                false
            )
            return DateModifiedHeaderViewHolder(layoutItemBinding)
        }
    }
}