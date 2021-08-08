package com.salesground.zipbolt.viewmodel

import android.net.Uri
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

    private val _newReceivedItemPosition = MutableLiveData<Int>(-1)
    val newReceivedItemPosition: LiveData<Int>
        get() = _newReceivedItemPosition

    private val _ongoingReceiveDataItem = MutableLiveData<ReceivedDataItem>(null)
    val ongoingReceiveDataItem: LiveData<ReceivedDataItem>
        get() = _ongoingReceiveDataItem


    fun addDataToReceivedItems(dataToTransfer: DataToTransfer) {
        receivedDataItemsNormalList.add(dataToTransfer)
        viewModelScope.launch(Dispatchers.Main) {
            _receivedDataItems.value = receivedDataItemsNormalList
            _newReceivedItemPosition.value = receivedDataItemsNormalList.size
        }
    }

    fun updateOngoingReceiveDataItem(receivedDataItem: ReceivedDataItem) {
        viewModelScope.launch {
            _ongoingReceiveDataItem.value = receivedDataItem
        }
    }
}

data class ReceivedDataItem(
    val dataDisplayName: String,
    val dataSize: Long,
    val percentageOfDataRead: Float,
    val dataType: Int,
    val dataUri: Uri?,
)