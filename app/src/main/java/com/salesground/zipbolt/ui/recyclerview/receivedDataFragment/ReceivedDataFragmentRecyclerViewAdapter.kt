package com.salesground.zipbolt.ui.recyclerview.receivedDataFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application.ApplicationReceiveCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio.AudioReceiveCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.directory.DirectoryReceiveCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.receivedDataFragment.viewHolders.image.ImageReceiveCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.video.VideoReceiveCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.receivedDataFragment.viewHolders.plainFile.document.PlainDocumentFileReceiveCompleteLayoutItemViewHolder

class ReceivedDataFragmentRecyclerViewAdapter :
    ListAdapter<DataToTransfer, RecyclerView.ViewHolder>
        (DataToTransferRecyclerViewDiffUtil()) {
    enum class ReceiveDataFragmentAdapterViewTypes(val value: Int) {
        IMAGE_RECEIVE_COMPLETE(1),
        VIDEO_RECEIVE_COMPLETE(2),
        APP_RECEIVE_COMPLETE(3),
        AUDIO_RECEIVE_COMPLETE(4),
        DIRECTORY_RECEIVE_COMPLETE(5),
        PLAIN_IMAGE_FILE_RECEIVE_COMPLETE(6),
        PLAIN_VIDEO_FILE_RECEIVE_COMPLETE(7),
        PLAIN_APP_FILE_RECEIVE_COMPLETE(8),
        PLAIN_AUDIO_FILE_RECEIVE_COMPLETE(9),
        PLAIN_DOCUMENT_FILE_RECEIVE_COMPLETE(10)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val dataItem = currentList[position]) {
            is DataToTransfer.DeviceImage -> {
                ReceiveDataFragmentAdapterViewTypes.IMAGE_RECEIVE_COMPLETE.value
            }
            is DataToTransfer.DeviceApplication -> {
                ReceiveDataFragmentAdapterViewTypes.APP_RECEIVE_COMPLETE.value
            }
            is DataToTransfer.DeviceAudio -> {
                ReceiveDataFragmentAdapterViewTypes.AUDIO_RECEIVE_COMPLETE.value
            }
            is DataToTransfer.DeviceVideo -> {
                ReceiveDataFragmentAdapterViewTypes.VIDEO_RECEIVE_COMPLETE.value
            }
            is DataToTransfer.DeviceFile -> {
                if (dataItem.file.isDirectory) {
                    ReceiveDataFragmentAdapterViewTypes.DIRECTORY_RECEIVE_COMPLETE.value
                } else {
                    when (dataItem.dataType) {
                        MediaType.File.ImageFile.value -> ReceiveDataFragmentAdapterViewTypes.PLAIN_IMAGE_FILE_RECEIVE_COMPLETE.value
                        MediaType.File.VideoFile.value -> ReceiveDataFragmentAdapterViewTypes.PLAIN_VIDEO_FILE_RECEIVE_COMPLETE.value
                        MediaType.File.AudioFile.value -> ReceiveDataFragmentAdapterViewTypes.PLAIN_AUDIO_FILE_RECEIVE_COMPLETE.value
                        else -> ReceiveDataFragmentAdapterViewTypes.PLAIN_DOCUMENT_FILE_RECEIVE_COMPLETE.value
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ReceiveDataFragmentAdapterViewTypes.IMAGE_RECEIVE_COMPLETE.value -> {
                ImageReceiveCompleteLayoutViewHolder.createViewHolder(parent)
            }
            ReceiveDataFragmentAdapterViewTypes.APP_RECEIVE_COMPLETE.value -> {
                ApplicationReceiveCompleteLayoutViewHolder.createViewHolder(parent)
            }
            ReceiveDataFragmentAdapterViewTypes.AUDIO_RECEIVE_COMPLETE.value -> {
                AudioReceiveCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            ReceiveDataFragmentAdapterViewTypes.VIDEO_RECEIVE_COMPLETE.value -> {
                VideoReceiveCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            ReceiveDataFragmentAdapterViewTypes.DIRECTORY_RECEIVE_COMPLETE.value -> {
                DirectoryReceiveCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            ReceiveDataFragmentAdapterViewTypes.PLAIN_DOCUMENT_FILE_RECEIVE_COMPLETE.value -> {
                PlainDocumentFileReceiveCompleteLayoutItemViewHolder.createViewHolder(parent)
            }

            else -> ImageReceiveCompleteLayoutViewHolder.createViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = currentList[position]
        when (holder) {
            is ImageReceiveCompleteLayoutViewHolder -> {
                holder.bindImageData(dataItem)
            }
            is ApplicationReceiveCompleteLayoutViewHolder -> {
                holder.bindData(dataItem)
            }
            is AudioReceiveCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is VideoReceiveCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is DirectoryReceiveCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is PlainDocumentFileReceiveCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
        }
    }
}