package com.salesground.zipbolt.ui.recyclerview.sentDataFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.OngoingDataTransferUIState
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.OngoingDataTransferRecyclerViewAdapter
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application.ApplicationTransferCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application.ApplicationTransferWaitingViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio.AudioTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio.AudioTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.directory.DirectoryTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.directory.DirectoryTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.image.ImageTransferCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.image.ImageTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.video.VideoTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.video.VideoTransferWaitingLayoutItemViewHolder

class SentDataFragmentRecyclerViewAdapter : ListAdapter<
        DataToTransfer, RecyclerView.ViewHolder>(
    DataToTransferRecyclerViewDiffUtil()
) {
    enum class SentDataFragmentAdapterViewTypes(val value: Int) {
        IMAGE_TRANSFER_WAITING(1),
        IMAGE_TRANSFER_COMPLETE(2),
        VIDEO_TRANSFER_WAITING(3),
        VIDEO_TRANSFER_COMPLETE(4),
        APP_TRANSFER_WAITING(6),
        APP_TRANSFER_COMPLETE(7),
        AUDIO_TRANSFER_WAITING(8),
        AUDIO_TRANSFER_COMPLETE(9),
        DIRECTORY_TRANSFER_WAITING(12),
        DIRECTORY_TRANSFER_COMPLETE(13)
    }

    override fun getItemViewType(position: Int): Int {
        val dataItem = (getItem(position) as OngoingDataTransferUIState.DataItem)
        return when (dataItem.dataToTransfer.transferStatus) {
            DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                when (dataItem.dataToTransfer) {
                    is DataToTransfer.DeviceImage -> {
                        SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_WAITING.value
                    }
                    is DataToTransfer.DeviceApplication -> {
                        SentDataFragmentAdapterViewTypes.APP_TRANSFER_WAITING.value
                    }
                    is DataToTransfer.DeviceAudio -> {
                        SentDataFragmentAdapterViewTypes.AUDIO_TRANSFER_WAITING.value
                    }
                    is DataToTransfer.DeviceVideo -> {
                        SentDataFragmentAdapterViewTypes.VIDEO_TRANSFER_WAITING.value
                    }
                    is DataToTransfer.DeviceFile -> {
                        if (dataItem.dataToTransfer.file.isDirectory) {
                            SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_WAITING.value
                        } else {
                            300
                        }
                    }
                }
            }
            DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                when (dataItem.dataToTransfer) {
                    is DataToTransfer.DeviceImage -> {
                        SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_COMPLETE.value
                    }
                    is DataToTransfer.DeviceApplication -> {
                        SentDataFragmentAdapterViewTypes.APP_TRANSFER_COMPLETE.value
                    }
                    is DataToTransfer.DeviceAudio -> {
                        SentDataFragmentAdapterViewTypes.AUDIO_TRANSFER_COMPLETE.value
                    }
                    is DataToTransfer.DeviceVideo -> {
                        SentDataFragmentAdapterViewTypes.VIDEO_TRANSFER_COMPLETE.value
                    }
                    is DataToTransfer.DeviceFile -> {
                        if (dataItem.dataToTransfer.file.isDirectory) {
                            SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value
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
            SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_WAITING.value -> {
                ImageTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_COMPLETE.value -> {
                ImageTransferCompleteLayoutViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.VIDEO_TRANSFER_WAITING.value -> {
                VideoTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.VIDEO_TRANSFER_COMPLETE.value -> {
                VideoTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.APP_TRANSFER_COMPLETE.value -> {
                ApplicationTransferCompleteLayoutViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.APP_TRANSFER_WAITING.value -> {
                ApplicationTransferWaitingViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.AUDIO_TRANSFER_COMPLETE.value -> {
                AudioTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.AUDIO_TRANSFER_WAITING.value -> {
                AudioTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_WAITING.value -> {
                DirectoryTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value -> {
                DirectoryTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            else -> {
                ImageTransferCompleteLayoutViewHolder.createViewHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = currentList[position]
        when (holder) {
            is ImageTransferCompleteLayoutViewHolder -> {
                holder.bindImageData(dataItem)
            }
            is ImageTransferWaitingLayoutItemViewHolder -> {
                holder.bindImageData(dataItem)
            }
            is ApplicationTransferWaitingViewHolder -> {
                holder.bindData(dataItem)
            }
            is ApplicationTransferCompleteLayoutViewHolder -> {
                holder.bindData(dataItem)
            }
            is VideoTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is VideoTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is AudioTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is AudioTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is DirectoryTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is DirectoryTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
        }
    }
}