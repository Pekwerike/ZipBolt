package com.salesground.zipbolt

import com.salesground.zipbolt.utils.customizeDate
import com.salesground.zipbolt.utils.parseDate
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class TimeUtilsTest {

    @Test
    fun test_parseDate() {
        assertEquals("20 March, 2021", System.currentTimeMillis().parseDate())
    }

    @Test
    fun test_dateDifference() {
        val yesterday = System.currentTimeMillis() - (24 * 60 * 60 * 1000)

        assertEquals("Yesterday", yesterday.parseDate().customizeDate())
    }
}