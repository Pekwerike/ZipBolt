package com.salesground.zipbolt.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import com.salesground.zipbolt.R

class ConnectionUtils {
    companion object {
        const val TRANSFER_TYPE_EXTRA = "TransferType"
        const val TRANSFER_TYPE_SEND_AND_RECEIVE = "SendAndReceive"
        const val TRANSFER_TYPE_SEND = "Send"
        const val ZIP_BOLT_TRANSFER_SERVICE = "zipbolt_transfer_service.tcp"

        @SuppressLint("MissingPermission", "HardwareIds")
        private fun createWifiDirectGroup(
            wifiP2pManager: WifiP2pManager,
            wifiP2pChannel: WifiP2pManager.Channel,
            wifiP2pActionListener: WifiP2pManager.ActionListener
        ) {
            // local service addition was successfully sent to the android framework
            wifiP2pManager.createGroup(wifiP2pChannel, wifiP2pActionListener)
        }


        @SuppressLint("MissingPermission")
        fun advertiseWifiP2pService(
            context: Context,
            transferType: String,
            wifiP2pManager: WifiP2pManager,
            wifiP2pChannel: WifiP2pManager.Channel,
            actionListener: WifiP2pManager.ActionListener
        ) {

            val record: Map<String, String> = mapOf(
                TRANSFER_TYPE_EXTRA to transferType
            )

            val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
                context.getString(R.string.zip_bolt_file_transfer_service),
                ZIP_BOLT_TRANSFER_SERVICE,
                record
            )
            wifiP2pManager.addLocalService(wifiP2pChannel, serviceInfo, actionListener)
        }

        fun discoverWifiP2pService(
            context: Context,
            transferType: String,
            wifiP2pManager: WifiP2pManager,
            wifiP2pChannel: WifiP2pManager.Channel,
            actionListener: WifiP2pManager.ActionListener
        ) {
            val deviceTransferTypeMap = HashMap<String, String>()
            val recordListener =
                WifiP2pManager.DnsSdTxtRecordListener { fullDomainName, txtRecordMap, srcDevice ->
                    deviceTransferTypeMap.put(
                        srcDevice.deviceAddress,
                        txtRecordMap[TRANSFER_TYPE_EXTRA] ?: TRANSFER_TYPE_SEND
                    )
                }
            val serviceListener =
                WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, srcDevice ->
                    if (instanceName == context.getString(R.string.zip_bolt_file_transfer_service)) {
                       val transferType = deviceTransferTypeMap[srcDevice.deviceAddress]
                        if(transferType == TRANSFER_TYPE_SEND_AND_RECEIVE){
                            // display this device to the user
                        }
                    }
                }
            wifiP2pManager.setDnsSdResponseListeners(wifiP2pChannel, serviceListener, recordListener)
            wifiP2pManager.addServiceRequest(wifiP2pChannel,
                WifiP2pDnsSdServiceRequest.newInstance(),
            actionListener)
        }
    }
}