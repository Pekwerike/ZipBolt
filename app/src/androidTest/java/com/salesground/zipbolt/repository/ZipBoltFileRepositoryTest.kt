package com.salesground.zipbolt.repository

import com.salesground.zipbolt.repository.implementation.ZipBoltFileRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class ZipBoltFileRepositoryTest {

    private lateinit var zipBoltFileRepository: FileRepository

    @Before
    fun setUp() {
        zipBoltFileRepository = ZipBoltFileRepository(context)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getRootDirectory() {
    }

    @Test
    fun getDirectoryChildren() {
    }

    @Test
    fun insertDirectory() {
    }

    @Test
    fun insertFile() {
    }
}