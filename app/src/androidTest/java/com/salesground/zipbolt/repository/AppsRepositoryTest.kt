Ipackage com.salesground.zipbolt.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class AppsRepositoryTest {

    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var appsRepository: AppsRepository

    @Before
    fun setUp() {
        appsRepository = AppsRepository(applicationContext)
    }

    // confirm that the mutable list allAppsOnDevice holds items
    @Test
    fun test_getAllAppsOnDevice_returnsAListOfApps() {
        val allAppsOnDevice = appsRepository.getAllAppsOnDevice()
        assertTrue(allAppsOnDevice.isNotEmpty())
    }

    @Test
    fun test_getAllAppsOnDevice_returnsOnlyAppsInstalledByTheUserInAndroidVersionGreaterThan8() {
        val allAppsOnDevice = appsRepository.getAllAppsOnDevice()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allAppsOnDevice.forEach {
                assertNotEquals(it.flags, ApplicationInfo.FLAG_SYSTEM)
            }
        }
    }

    // confirm that all apps on device has an icon
    @Test
    fun test_thatAllAppsOnDeviceHasAnIcon() {
        val allAppsOnDevice = appsRepository.getAllAppsOnDevice()

        allAppsOnDevice.forEach {
            assertNotNull(it.loadIcon(applicationContext.packageManager))
        }
    }

    // confirm that the getAllApplicationAsCustomModel returns the size of all applications
    @Test
    fun testThat_getAllApplicationAsCustomModelReturnsTheSizeOfAnApplication() = runBlocking {
        appsRepository.getAllApplicationAsCustomModel()
            .collect {
                assertTrue(it.appSize > 100)
            }
    }


}
