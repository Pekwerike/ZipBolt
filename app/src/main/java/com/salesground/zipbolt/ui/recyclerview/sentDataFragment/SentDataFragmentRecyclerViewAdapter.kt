package com.salesground.zipbolt.ui.recyclerview.sentDataFragment

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.model.MediaType
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewDiffUtil
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.application.ApplicationTransferCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.application.ApplicationTransferWaitingViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.audio.AudioTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.audio.AudioTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.directory.DirectoryTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.directory.DirectoryTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.image.ImageTransferCompleteLayoutViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.image.ImageTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile.PlainFileTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile.PlainFileTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile.video.PlainVideoFileTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.plainFile.video.PlainVideoFileTransferWaitingLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.video.VideoTransferCompleteLayoutItemViewHolder
import com.salesground.zipbolt.ui.recyclerview.sentDataFragment.viewHolders.video.VideoTransferWaitingLayoutItemViewHolder

class SentDataFragmentRecyclerViewAdapter() : ListAdapter<
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
        DIRECTORY_TRANSFER_COMPLETE(13),
        PLAIN_FILE_TRANSFER_WAITING(14),
        PLAIN_FILE_TRANSFER_COMPLETE(15),
        PLAIN_VIDEO_FILE_TRANSFER_WAITING(16),
        PLAIN_VIDEO_FILE_TRANSFER_COMPLETE(17)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val dataItem = currentList[position]) {
            is DataToTransfer.DeviceImage -> {
                when (dataItem.transferStatus) {
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                        SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_COMPLETE.value
                    }
                    DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                        SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_WAITING.value
                    }
                    else -> SentDataFragmentAdapterViewTypes.IMAGE_TRANSFER_COMPLETE.value
                }
            }
            is DataToTransfer.DeviceApplication -> {
                when (dataItem.transferStatus) {
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                        SentDataFragmentAdapterViewTypes.APP_TRANSFER_COMPLETE.value
                    }
                    DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                        SentDataFragmentAdapterViewTypes.APP_TRANSFER_WAITING.value
                    }
                    else -> SentDataFragmentAdapterViewTypes.APP_TRANSFER_COMPLETE.value
                }
            }
            is DataToTransfer.DeviceAudio -> {
                when (dataItem.transferStatus) {
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                        SentDataFragmentAdapterViewTypes.AUDIO_TRANSFER_COMPLETE.value
                    }
                    DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                        SentDataFragmentAdapterViewTypes.AUDIO_TRANSFER_WAITING.value
                    }
                    else -> SentDataFragmentAdapterViewTypes.AUDIO_TRANSFER_COMPLETE.value
                }
            }
            is DataToTransfer.DeviceVideo -> {
                when (dataItem.transferStatus) {
                    DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                        SentDataFragmentAdapterViewTypes.VIDEO_TRANSFER_COMPLETE.value
                    }
                    DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                        SentDataFragmentAdapterViewTypes.VIDEO_TRANSFER_WAITING.value
                    }
                    else -> SentDataFragmentAdapterViewTypes.VIDEO_TRANSFER_COMPLETE.value
                }
            }
            is DataToTransfer.DeviceFile -> {
                if (dataItem.file.isDirectory) {
                    when (dataItem.transferStatus) {
                        DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                            SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value
                        }
                        DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                            SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_WAITING.value
                        }
                        else -> SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value
                    }
                } else {
                    when (dataItem.transferStatus) {
                        DataToTransfer.TransferStatus.TRANSFER_COMPLETE -> {
                            when (dataItem.dataType) {
                                MediaType.File.VideoFile.value -> SentDataFragmentAdapterViewTypes.PLAIN_VIDEO_FILE_TRANSFER_COMPLETE.value
                                else -> SentDataFragmentAdapterViewTypes.PLAIN_FILE_TRANSFER_COMPLETE.value
                            }

                        }
                        DataToTransfer.TransferStatus.TRANSFER_WAITING -> {
                            when (dataItem.dataType) {
                                MediaType.File.VideoFile.value -> SentDataFragmentAdapterViewTypes.PLAIN_VIDEO_FILE_TRANSFER_WAITING.value
                                else -> SentDataFragmentAdapterViewTypes.PLAIN_FILE_TRANSFER_WAITING.value
                            }
                        }
                        else -> SentDataFragmentAdapterViewTypes.DIRECTORY_TRANSFER_COMPLETE.value
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
            SentDataFragmentAdapterViewTypes.PLAIN_FILE_TRANSFER_COMPLETE.value -> {
                PlainFileTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.PLAIN_FILE_TRANSFER_WAITING.value -> {
                PlainFileTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.PLAIN_VIDEO_FILE_TRANSFER_COMPLETE.value -> {
                PlainVideoFileTransferCompleteLayoutItemViewHolder.createViewHolder(parent)
            }
            SentDataFragmentAdapterViewTypes.PLAIN_VIDEO_FILE_TRANSFER_WAITING.value -> {
                PlainVideoFileTransferWaitingLayoutItemViewHolder.createViewHolder(parent)
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
            is PlainFileTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is PlainFileTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is PlainVideoFileTransferCompleteLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
            is PlainVideoFileTransferWaitingLayoutItemViewHolder -> {
                holder.bindData(dataItem)
            }
        }
    }
}