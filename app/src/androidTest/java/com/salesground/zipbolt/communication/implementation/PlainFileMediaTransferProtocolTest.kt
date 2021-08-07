package com.salesground.zipbolt.communication.implementation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import org.junit.Assert.*
import java.io.*

class PlainFileMediaTransferProtocolTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val gateWayFile = File(
        context.getExternalFilesDir(null),
        "gateWay.txt"
    )
    private val gateWayFileOutputStream =
        DataOutputStream(BufferedOutputStream(FileOutputStream(gateWayFile)))
    private val gateWayFileInputStream =
        DataInputStream(BufferedInputStream(FileInputStream(gateWayFile)))

    private val plainFileMediaTransferProtocol: PlainFileMediaTransferProtocol =
        PlainFileMediaTransferProtocol(ZipBoltSavedFilesRepository())


}