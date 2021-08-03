package com.salesground.zipbolt.ui.recyclerview.receivedDataFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.OngoingDataTransferUIState
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application.ApplicationReceiveCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio.AudioReceiveCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.directory.DirectoryReceiveCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.image.ImageReceiveCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.video.VideoReceiveCompleteLayoutItemViewHolder

class ReceivedDataFragmentRecyclerViewAdapter :
    ListAdapter<DataToTransfer, RecyclerView.ViewHolder>
        (DataToTransferRecyclerViewDiffUtil()) {
    enum class ReceiveDataFragmentAdapterViewTypes(val value: Int) {
        IMAGE_RECEIVE_COMPLETE(1),
        VIDEO_RECEIVE_COMPLETE(2),
        APP_RECEIVE_COMPLETE(3),
        AUDIO_RECEIVE_COMPLETE(4),
        DIRECTORY_RECEIVE_COMPLETE(5)
    }

    override fun getItemViewType(position: Int): Int {
        val dataItem = (getItem(position) as OngoingDataTransferUIState.DataItem)
        return when (dataItem.dataToTransfer.transferStatus) {
            DataToTransfer.TransferStatus.RECEIVE_COMPLETE -> {
                when (dataItem.dataToTransfer) {
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
                        if (dataItem.dataToTransfer.file.isDirectory) {
                            ReceiveDataFragmentAdapterViewTypes.DIRECTORY_RECEIVE_COMPLETE.value
                        } else {
                            300
                        }
                    }
                }
            }
            else -> 300
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
        }
    }
}