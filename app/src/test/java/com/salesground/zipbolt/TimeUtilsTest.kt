package com.salesground.zipbolt

import com.salesground.zipbolt.utils.parseDate
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeUtilsTest {

    @Test
    fun test_parseDate(){
        val currentTime = System.currentTimeMillis()
        assertEquals("March, 2021", currentTime.parseDate())
    }
}