package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.OngoingDataTransferUIState
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.image.ImageTransferOrReceiveCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.image.ImageTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.OngoingTransferCategoryHeaderViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application.ApplicationTransferOngoingViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.application.ApplicationTransferOrReceiveCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio.AudioTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.audio.AudioTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.directory.DirectoryTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.directory.DirectoryTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.video.VideoTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.video.VideoTransferWaitingLayoutItemViewHolder

class OngoingDataTransferRecyclerViewAdapter : ListAdapter<OngoingDataTransferUIState,
        RecyclerView.ViewHolder>(OngoingDataTransferRecyclerViewAdapterDiffUtil) {

    enum class OngoingDataTransferAdapterViewTypes(val value: Int) {
        CATEGORY_HEADER(11),
        IMAGE_TRANSFER_WAITING(1),
        IMAGE_TRANSFER_OR_RECEIVE_COMPLETE(2),
        VIDEO_TRANSFER_WAITING(3),
        VIDEO_TRANSFER_COMPLETE(4),
        VIDEO_RECEIVE_COMPLETE(5),
        APP_TRANSFER_WAITING(6),
        APP_TRANSFER_OR_RECEIVE_COMPLETE(7),
        AUDIO_TRANSFER_WAITING(8),
        AUDIO_TRANSFER_COMPLETE(9),
        AUDIO_RECEIVE_COMPLETE(10),
        DIRECTORY_TRANSFER_WAITING(12),
        DIRECTORY_TRANSFER_COMPLETE(13),
        DIRECTORY_RECEIVE_COMPLETE(14)
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> OngoingDataTransferAdapterViewTypes.CATEGORY_HEADER.value
            else -> {
                val dataItem = (getItem(position) as OngoingDataTransferUIState.DataItem)
                when (dataItem.dataToTransfer.transferStatus) {
                    DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_WAITING.value
                            }
                            is DataToTransfer.DeviceApplication -> {
                                OngoingDataTransferAdapterViewTypes.APP_TRANSFER_WAITING.value
                            }
                            is DataToTransfer.DeviceAudio -> {
                                OngoingDataTransferAdapterViewTypes.AUDIO_TRANSFER_WAITING.value
                            }
                            is DataToTransfer.DeviceVideo -> {
                                OngoingDataTransferAdapterViewTypes.VIDEO_TRANSFER_WAITING.value
                            }
                            is DataToTransfer.DeviceFile -> {
                                if (dataItem.dataToTransfer.file.isDirectory) {
                                    OngoingDataTransferAdapterViewTypes.DIRECTORY_TRANSFER_WAITING.value
                                } else {
                                    300
                                }
                            }
                        }
                    }
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_OR_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceApplication -> {
                                OngoingDataTransferAdapterViewTypes.APP_TRANSFER_OR_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceAudio -> {
                                OngoingDataTransferAdapterViewTypes.AUDIO_TRANSFER_COMPLETE.value
                            }
                            is DataToTransfer.DeviceVideo -> {
                                OngoingDataTransferAdapterViewTypes.VIDEO_TRANSFER_COMPLETE.value
                            }
                            is DataToTransfer.DeviceFile -> {
                                if (dataItem.dataToTransfer.file.isDirectory) {
                                    OngoingDataTransferAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value
                                } else {
                                    300
                                }
                            }
                        }
                    }
                    DataToTransfer.TransferStatus.RECEIVE_COMPLETE -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_OR_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceApplication -> {
                                OngoingDataTransferAdapterViewTypes.APP_TRANSFER_OR_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceAudio -> {
                                OngoingDataTransferAdapterViewTypes.AUDIO_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceVideo -> {
                                OngoingDataTransferAdapterViewTypes.VIDEO_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceFile -> {
                                if (dataItem.dataToTransfer.file.isDirectory) {
                                    OngoingDataTransferAdapterViewTypes.DIRECTORY_RECEIVE_COMPLETE.value
                                } else {
                                    300
                                }
                            }
                        }
                    }
                    else -> 300
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_WAITING.value -> {
                ImageTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }
            OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_OR_RECEIVE_COMPLETE.value -> {
                ImageTransferOrReceiveCompleteLayoutViewHolder.createViewHolder(parent)
            }
            OngoingDataTransferAdapterViewTypes.CATEGORY_HEADER.value -> {
                OngoingTransferCategoryHeaderViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.APP_TRANSFER_OR_RECEIVE_COMPLETE.value -> {
                ApplicationTransferOrReceiveCompleteLayoutViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.APP_TRANSFER_WAITING.value -> {
                ApplicationTransferOngoingViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.VIDEO_TRANSFER_WAITING.value -> {
                VideoTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.VIDEO_TRANSFER_COMPLETE.value -> {
                VideoTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.VIDEO_RECEIVE_COMPLETE.value -> {
                VideoTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.AUDIO_TRANSFER_WAITING.value -> {
                AudioTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.AUDIO_TRANSFER_COMPLETE.value -> {
                AudioTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.AUDIO_RECEIVE_COMPLETE.value -> {
                AudioTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value -> {
                DirectoryTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            OngoingDataTransferAdapterViewTypes.DIRECTORY_TRANSFER_WAITING.value -> {
                DirectoryTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }

            OngoingDataTransferAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value -> {
                DirectoryTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            else -> {
                ImageTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataToTransfer: DataToTransfer =
            (currentList[position] as OngoingDataTransferUIState.DataItem).dataToTransfer
        when (holder) {
            is ImageTransferOrReceiveCompleteLayoutViewHolder -> {
                holder.bindImageData(dataToTransfer)
            }
            is ImageTransferWaitingLayoutItemViewHolder -> {
                holder.bindImageData(dataToTransfer)
            }
            is ApplicationTransferOngoingViewHolder -> {
                holder.bindData(dataToTransfer)
            }
            is ApplicationTransferOrReceiveCompleteLayoutViewHolder -> {
                holder.bindData(dataToTransfer)
            }
            is VideoTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataToTransfer)
            }
            is VideoTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataToTransfer)
            }
            is AudioTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataToTransfer)
            }
            is AudioTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataToTransfer)
            }
            is DirectoryTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataToTransfer)
            }
            is DirectoryTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataToTransfer)
            }
        }
    }

    object OngoingDataTransferRecyclerViewAdapterDiffUtil :
        DiffUtil.ItemCallback<OngoingDataTransferUIState>() {
        override fun areItemsTheSame(
            oldItem: OngoingDataTransferUIState,
            newItem: OngoingDataTransferUIState
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OngoingDataTransferUIState,
            newItem: OngoingDataTransferUIState
        ): Boolean {
            return oldItem == newItem
        }
    }
}
