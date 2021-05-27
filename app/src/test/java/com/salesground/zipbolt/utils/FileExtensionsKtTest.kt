package com.salesground.zipbolt.utils

import androidx.core.math.MathUtils
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.MathContext

@RunWith(JUnit4::class)
class FileExtensionsKtTest {

    @Test
    fun transformDataSizeToMeasuredUnit() {
        assertEquals("1.2mb", 1200000L.transformDataSizeToMeasuredUnit())
        assertEquals("1.3mb", 1290000L.transformDataSizeToMeasuredUnit())
        assertEquals("13.1mb", 13091000L.transformDataSizeToMeasuredUnit())
    }
}