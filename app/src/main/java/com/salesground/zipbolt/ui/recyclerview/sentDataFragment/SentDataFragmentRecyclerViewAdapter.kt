package com.salesground.zipbolt.ui.recyclerview.sentDataFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.ui.OngoingDataTransferUIState
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.ongoingDataTransferRecyclerViewComponents.OngoingDataTransferRecyclerViewAdapter

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
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}