package com.salesground.zipbolt.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ReceivedDataViewModel : ViewModel() {

    var currentReceiveDataToTransferItem: DataToTransfer = DataToTransfer.DeviceFile(
        File(
            ""
        )
    )
    private val receivedDataItemsNormalList: MutableList<DataToTransfer> = mutableListOf()
    private val _receivedDataItems = MutableLiveData<MutableList<DataToTransfer>>(mutableListOf())
    val receivedDataItems: LiveData<MutableList<DataToTransfer>>
        get() = _receivedDataItems

    private val _newReceivedItemPosition = MutableLiveData<Int>(-1)
    val newReceivedItemPosition: LiveData<Int>
        get() = _newReceivedItemPosition

    private val _ongoingDataReceivePercent = MutableLiveData<Float>(0f)
    val ongoingDataReceivePercent: LiveData<Float>
        get() = _ongoingDataReceivePercent

    private val _completedReceivedDataItem = MutableLiveData<DataToTransfer>(null)
    val completedReceivedDataItem: LiveData<DataToTransfer>
        get() = _completedReceivedDataItem

    private val _dataReceiveStartedDataItem = MutableLiveData<ReceivedDataItem>(null)
    val dataReceiveStartedDataItem: LiveData<ReceivedDataItem>
        get() = _dataReceiveStartedDataItem

    fun addDataToReceivedItems(dataToTransfer: DataToTransfer) {
        receivedDataItemsNormalList.add(dataToTransfer)
        viewModelScope.launch(Dispatchers.Main) {
            _completedReceivedDataItem.value = dataToTransfer
            _receivedDataItems.value = receivedDataItemsNormalList
            _newReceivedItemPosition.value = receivedDataItemsNormalList.size
        }
    }

    fun updateOngoingReceiveDataItemReceivePercent(receivePercent: Float) {
        viewModelScope.launch {
            _ongoingDataReceivePercent.value = receivePercent
        }
    }

    fun onDataReceiveStarted(receivedDataItem: ReceivedDataItem) {
        viewModelScope.launch {
            _dataReceiveStartedDataItem.value = receivedDataItem
        }
    }
}

data class ReceivedDataItem(
    val dataDisplayName: String,
    val dataSize: Long,
    val percentageOfDataRead: Float,
    val dataType: Int,
    val dataUri: Uri?
)