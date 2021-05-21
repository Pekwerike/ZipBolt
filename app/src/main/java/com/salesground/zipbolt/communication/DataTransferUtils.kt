package com.salesground.zipbolt.communication

import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.StringBuilder

object DataTransferUtils {
    private val messageBuilder = StringBuilder()
    var messageLength = 0

    /*fun writeSocketString(message: String, dataOutputStream: DataOutputStream) {
        dataOutputStream.writeInt(message.length)
        dataOutputStream.writeChars(message)
    }*/

 /*   fun readSocketString(dataInputStream: DataInputStream): String {
        messageBuilder.setLength(0)
        messageLength = dataInputStream.readInt()
        for (i in 0 until messageLength) {
            messageBuilder.append(dataInputStream.readChar())
        }
        return messageBuilder.toString()
    }*/
}