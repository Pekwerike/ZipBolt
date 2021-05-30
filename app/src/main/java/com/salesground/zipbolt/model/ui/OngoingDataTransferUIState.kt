package com.salesground.zipbolt.model.ui

import com.salesground.zipbolt.model.DataToTransfer

sealed class OngoingDataTransferUIState(val id: String) {


    object  Header : OngoingDataTransferUIState("Queue")
    data class DataItem(val dataToTransfer: DataToTransfer) :
        OngoingDataTransferUIState(dataToTransfer.dataUri.toString())
}
