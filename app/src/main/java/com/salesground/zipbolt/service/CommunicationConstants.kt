package com.salesground.zipbolt.service

const val NO_DATA_AVAILABLE = "NoDataAvailable"
const val DATA_AVAILABLE = "DataAvailable"
const val FILE_TRANSFER_FOREGROUND_NOTIFICATION_ID = 2
const val SOCKET_PORT = 8080


enum class DataTransferUserEvent(val state: String){
    NO_DATA("NoDataAvailable"),
    DATA_AVAILABLE("DataAvailable"),
    CANCEL_ON_GOING_TRANSFER("CancelOngoingTransfer")
}