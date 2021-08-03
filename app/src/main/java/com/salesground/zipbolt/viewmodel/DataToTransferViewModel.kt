package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.DataToTransfer

class DataToTransferViewModel : ViewModel() {

    private var collectionOfDataToTransfer: MutableList<DataToTransfer> = mutableListOf()

    private val sentDataItemsNormalList: MutableList<DataToTransfer> = mutableListOf()
    private val _sentDataItems = MutableLiveData<MutableList<DataToTransfer>>(mutableListOf())
    val sentDataItems: LiveData<MutableList<DataToTransfer>>
        get() = _sentDataItems

    private val _receivedDataItems = MutableLiveData<MutableList<DataToTransfer>>(mutableListOf())
    val receivedDataItems: LiveData<MutableList<DataToTransfer>>
        get() = _receivedDataItems

    fun addDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.add(dataToTransfer)
    }

    fun removeDataFromDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.remove(dataToTransfer)
    }

    fun clearCollectionOfDataToTransfer() {
        collectionOfDataToTransfer = mutableListOf()
    }

    fun getCollectionOfDataToTransfer(): MutableList<DataToTransfer> {
        return collectionOfDataToTransfer
    }

    fun addCollectionOfDataToTransferToSentDataItems() {
        sentDataItemsNormalList.addAll(collectionOfDataToTransfer.map {
            it.transferStatus = DataToTransfer.TransferStatus.TRANSFER_ONGOING
        } as MutableList<DataToTransfer>)

        _sentDataItems.value = sentDataItemsNormalList
    }

}