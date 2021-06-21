import com.salesground.zipbolt.repository.implementation.DeviceApplicationsRepository


import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.salesground.zipbolt.repository.ZipBoltSavedFilesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class DeviceApplicationsRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var deviceApplicationsRepository: DeviceApplicationsRepository

    @Before
    fun setUp() {
        deviceApplicationsRepository = DeviceApplicationsRepository(applicationContext,
        ZipBoltSavedFilesRepository())
    }

    // confirm that the mutable list allAppsOnDevice holds items
    @Test
    fun test_getAllAppsOnDevice_returnsAListOfApps() {
        runBlocking {
            val allAppsOnDevice = deviceApplicationsRepository.getAllAppsOnDevice()
            assertTrue(allAppsOnDevice.isNotEmpty())
        }
    }

    @Test
    fun test_getAllAppsOnDevice_returnsOnlyAppsInstalledByTheUserInAndroidVersionGreaterThan8() {
        runBlocking {
            val allAppsOnDevice = deviceApplicationsRepository.getAllAppsOnDevice()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                allAppsOnDevice.forEach {
                    assertNotEquals(it.flags, ApplicationInfo.FLAG_SYSTEM)
                }
            }
        }
    }

    // confirm that all apps on device has an icon
    @Test
    fun test_thatAllAppsOnDeviceHasAnIcon() { runBlocking {
        val allAppsOnDevice = deviceApplicationsRepository.getAllAppsOnDevice()

        allAppsOnDevice.forEach {
            assertNotNull(it.loadIcon(applicationContext.packageManager))
        }
    }
    }



}
