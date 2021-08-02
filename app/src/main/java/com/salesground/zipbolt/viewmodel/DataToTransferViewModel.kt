package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.DataToTransfer

class DataToTransferViewModel : ViewModel() {

    private var collectionOfDataToTransfer: MutableList<DataToTransfer> = mutableListOf()

    fun addDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.remove(dataToTransfer)
    }

    fun removeDataFromDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.remove(dataToTransfer)
    }

    fun clearCollectionOfDataToTranfer() {
        collectionOfDataToTransfer = mutableListOf()
    }

    fun getCollectionOfDataToTransfer(): MutableList<DataToTransfer> {
        return collectionOfDataToTransfer
    }
}