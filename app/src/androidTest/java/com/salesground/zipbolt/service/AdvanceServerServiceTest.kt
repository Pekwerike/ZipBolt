package com.salesground.zipbolt.service

import dagger.hilt.android.testing.HiltAndroidTest

@HiltAndroidTest
class AdvanceServerServiceTest {

   /* private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var baseTestDirectory: File
    private lateinit var gateWayOne: File
    private lateinit var gateWayOneDOS: DataOutputStream
    private lateinit var gateWayOneDIS: DataInputStream
    private lateinit var gateWayTwo: File
    private lateinit var gateWayTwoDOS: DataOutputStream
    private lateinit var gateWayTwoDIS: DataInputStream

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val serviceRule = ServiceTestRule()

    @Inject
    lateinit var imageRepository: ImageRepository

    private val clientMediaTransferProtocol = MediaTransferProtocolImpl(context,
    AdvanceImageRepository(context, ZipBoltSavedFilesRepository())
    )

    private val serverMediaTransferProtocol = MediaTransferProtocolImpl(context,
        AdvanceImageRepository(context, ZipBoltSavedFilesRepository())
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        baseTestDirectory = File(context.getExternalFilesDir(null), "BaseTestDirectory")
        if (!baseTestDirectory.exists()) baseTestDirectory.mkdirs()
        gateWayOne = File(baseTestDirectory, "gateWayOne.txt")
        gateWayOneDOS = DataOutputStream(FileOutputStream(gateWayOne))
        gateWayOneDIS = DataInputStream(FileInputStream(gateWayOne))
        gateWayTwo = File(baseTestDirectory, "gateWayTwo.txt")
        gateWayTwoDOS = DataOutputStream(FileOutputStream(gateWayTwo))
        gateWayTwoDIS = DataInputStream(FileInputStream(gateWayTwo))
    }

    @After
    fun tearDown() {
        gateWayOne.delete()
        gateWayTwo.delete()
        baseTestDirectory.delete()
    }

    @Test
    fun testBindService() = runBlocking {
        val imagesOnDevice = imageRepository.getImagesOnDevice().map {
            imageRepository.getMetaDataOfImage(it as DataToTransfer.DeviceImage)
        }
        val serverServiceIntent = Intent(context, DataTransferService::class.java).apply {
            putExtra(IS_SERVER_KEY, true)
        }
        val clientServiceIntent = Intent(context, DataTransferService::class.java).apply {
            putExtra(IS_SERVER_KEY, false)
            putExtra(SERVER_IP_ADDRESS_KEY, "192.168.43.190")
        }

        val serverServiceBinder: IBinder = serviceRule.bindService(serverServiceIntent)
        val clientServiceBinder: IBinder = serviceRule.bindService(clientServiceIntent)


        val serverService = (serverServiceBinder as
                DataTransferService.DataTransferServiceBinder).getServiceInstance()
        val clientService = (clientServiceBinder as
                DataTransferService.DataTransferServiceBinder).getServiceInstance()

        serverService.setUpMediaTransferProtocolForTestCase(serverMediaTransferProtocol)
        serverService.configureServerMock(
            dataOutputStream = gateWayOneDOS,
            dataInputStream = gateWayTwoDIS
        )

        clientService.setUpMediaTransferProtocolForTestCase(clientMediaTransferProtocol)
        clientService.configureClientMock(
            dataOutputStream = gateWayTwoDOS,
            dataInputStream = gateWayOneDIS
        )

        clientService.transferData(dataCollectionSelected =
        imagesOnDevice.takeLast(3).toMutableList())


        delay(20000)
        assertEquals(imagesOnDevice.size + 3, imageRepository.getImagesOnDevice().size)
    }*/
}