package com.salesground.zipbolt.model.ui

import com.salesground.zipbolt.model.DataToTransfer

sealed class OngoingDataTransferUIState(val id: String) {
    data class Header(val title: String) : OngoingDataTransferUIState(title)
    object NoItemInTransfer : OngoingDataTransferUIState("NoItemInTransfer")
    object NoItemInReceive : OngoingDataTransferUIState("NoItemInReceive")
    data class DataItem(val dataToTransfer: DataToTransfer) :
        OngoingDataTransferUIState(dataToTransfer.dataUri.toString())
}
