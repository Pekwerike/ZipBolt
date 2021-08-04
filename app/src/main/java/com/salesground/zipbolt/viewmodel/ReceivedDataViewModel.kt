package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceivedDataViewModel : ViewModel() {

    private val receivedDataItemsNormalList: MutableList<DataToTransfer> = mutableListOf()
    private val _receivedDataItems = MutableLiveData<MutableList<DataToTransfer>>(mutableListOf())
    val receivedDataItems: LiveData<MutableList<DataToTransfer>>
        get() = _receivedDataItems

    fun addDataToReceivedItems(dataToTransfer: DataToTransfer) {
        receivedDataItemsNormalList.add(dataToTransfer)
        viewModelScope.launch(Dispatchers.Main) {
            _receivedDataItems.value = receivedDataItemsNormalList
        }
    }
}