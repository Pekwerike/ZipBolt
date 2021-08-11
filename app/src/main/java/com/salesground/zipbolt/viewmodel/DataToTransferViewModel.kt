package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.utils.SingleLiveDataEventForUIState

class DataToTransferViewModel : ViewModel() {

    private val _sentDataButtonClicked = MutableLiveData<SingleLiveDataEventForUIState<Boolean>>()
    val sentDataButtonClicked: LiveData<SingleLiveDataEventForUIState<Boolean>>
        get() = _sentDataButtonClicked

    private var _collectionOfDataToTransfer: MutableList<DataToTransfer> = mutableListOf()
    val collectionOfDataToTransfer: MutableList<DataToTransfer>
        get() = _collectionOfDataToTransfer

    fun addDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.add(dataToTransfer)
    }

    fun removeDataFromDataToTransfer(dataToTransfer: DataToTransfer) {
        collectionOfDataToTransfer.remove(dataToTransfer)
    }

    fun clearCollectionOfDataToTransfer() {
        _collectionOfDataToTransfer = mutableListOf()
    }

    fun sentDataButtonClicked() {
        _sentDataButtonClicked.value = SingleLiveDataEventForUIState(true)
    }

}