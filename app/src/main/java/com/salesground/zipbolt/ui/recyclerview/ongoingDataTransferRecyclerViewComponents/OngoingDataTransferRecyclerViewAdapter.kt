package com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.OngoingDataTransferUIState
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.ImageTransferOrReceiveCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.ImageTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.NoItemInTransferOrReceiveViewHolder
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.viewHolders.OngoingTransferCategoryHeaderViewHolder
import com.salesground.zipbolt.ui.recyclerview.expandedconnectedtopeertransferongoing.OngoingDataTransferViewHolder
import java.lang.Exception

class OngoingDataTransferRecyclerViewAdapter : ListAdapter<OngoingDataTransferUIState,
        RecyclerView.ViewHolder>(OngoingDataTransferRecyclerViewAdapterDiffUtil) {

    enum class OngoingDataTransferAdapterViewTypes(val value: Int) {
        CATEGORY_HEADER(11),
        ACTIVE_DATA_TRANSFER(12),
        IMAGE_TRANSFER_WAITING(1),
        IMAGE_TRANSFER_OR_RECEIVE_COMPLETE(2),
        VIDEO_TRANSFER_WAITING(3),
        VIDEO_TRANSFER_COMPLETE(4),
        VIDEO_RECEIVE_COMPLETE(5),
        APP_TRANSFER_WAITING(6),
        APP_TRANSFER_COMPLETE(7),
        APP_RECEIVE_COMPLETE(9),
        NO_ITEM_IN_TRANSFER(13),
        NO_ITEM_IN_RECEIVE(14)
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> OngoingDataTransferAdapterViewTypes.CATEGORY_HEADER.value
            1 -> when (getItem(position)) {
                is OngoingDataTransferUIState.DataItem -> {
                    OngoingDataTransferAdapterViewTypes.ACTIVE_DATA_TRANSFER.value
                }
                is OngoingDataTransferUIState.NoItemInTransfer -> {
                    OngoingDataTransferAdapterViewTypes.NO_ITEM_IN_TRANSFER.value
                }
                else -> OngoingDataTransferAdapterViewTypes.NO_ITEM_IN_TRANSFER.value
            }
            2 -> OngoingDataTransferAdapterViewTypes.CATEGORY_HEADER.value
            3 -> when (getItem(position)) {
                is OngoingDataTransferUIState.DataItem -> {
                    OngoingDataTransferAdapterViewTypes.ACTIVE_DATA_TRANSFER.value
                }
                is OngoingDataTransferUIState.NoItemInTransfer -> {
                    OngoingDataTransferAdapterViewTypes.NO_ITEM_IN_RECEIVE.value
                }
                else -> OngoingDataTransferAdapterViewTypes.NO_ITEM_IN_RECEIVE.value
            }
            4 -> OngoingDataTransferAdapterViewTypes.CATEGORY_HEADER.value
            else -> {
                val dataItem = (getItem(position) as OngoingDataTransferUIState.DataItem)
                when (dataItem.dataToTransfer.transferStatus) {
                    DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_WAITING.value
                            }
                            is DataToTransfer.DeviceApplication -> {
                                300
                            }
                            is DataToTransfer.DeviceAudio -> {
                                300
                            }
                            is DataToTransfer.DeviceVideo -> {
                                300
                            }
                        }
                    }
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_OR_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceApplication -> {
                                300
                            }
                            is DataToTransfer.DeviceAudio -> {
                                300
                            }
                            is DataToTransfer.DeviceVideo -> {
                                300
                            }
                        }
                    }
                    DataToTransfer.TransferStatus.RECEIVE_COMPLETE -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                OngoingDataTransferAdapterViewTypes.IMAGE_TRANSFER_OR_RECEIVE_COMPLETE.value
                            }
                            is DataToTransfer.DeviceApplication -> {
                                300
                            }
                            is DataToTransfer.DeviceAudio -> {
                                300
                            }
                            is DataToTransfer.DeviceVideo -> {
                                300
                            }
                        }
                    }
                    DataToTransfer.TransferStatus.RECEIVE_ONGOING -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                OngoingDataTransferAdapterViewTypes.ACTIVE_DATA_TRANSFER.value
                            }
                            is DataToTransfer.DeviceApplication -> {
                                300
                            }
                            is DataToTransfer.DeviceAudio -> {
                                300
                            }
                            is DataToTransfer.DeviceVideo -> {
                                300
                            }
                        }
                    }
                    DataToTransfer.TransferStatus.NO_ACTION -> {
                        when (dataItem.dataToTransfer) {
                            is DataToTransfer.DeviceImage -> {
                                300
                            }
                            is DataToTransfer.DeviceApplication -> {
                                300
                            }
                            is DataToTransfer.DeviceAudio -> {
                                300
                            }
                            is DataToTransfer.DeviceVideo -> {
                                300
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
            OngoingDataTransferAdapterViewTypes.ACTIVE_DATA_TRANSFER.value -> {
                OngoingDataTransferViewHolder.createViewHolder(parent)
            }
            OngoingDataTransferAdapterViewTypes.NO_ITEM_IN_TRANSFER.value -> {
                NoItemInTransferOrReceiveViewHolder.createViewHolder(parent)
            }
            OngoingDataTransferAdapterViewTypes.NO_ITEM_IN_RECEIVE.value -> {
                NoItemInTransferOrReceiveViewHolder.createViewHolder(parent)
            }
            else -> {
                throw Exception()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageTransferOrReceiveCompleteLayoutViewHolder -> {
                holder.bindImageData((currentList[position] as OngoingDataTransferUIState.DataItem).dataToTransfer)
            }
            is ImageTransferWaitingLayoutItemViewHolder -> {
                holder.bindImageData((currentList[position] as OngoingDataTransferUIState.DataItem).dataToTransfer)
            }
            is OngoingTransferCategoryHeaderViewHolder -> {
                holder.bindCategoryHeaderTitle((currentList[position] as OngoingDataTransferUIState.Header).title)
            }
            is OngoingDataTransferViewHolder -> {
                holder.bindData((currentList[position] as OngoingDataTransferUIState.DataItem).dataToTransfer)
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