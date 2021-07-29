package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject


@HiltViewModel
class FileViewModel @Inject constructor(
    private val filesRepository: FileRepository
) : ViewModel() {
    private val directoryStack: Deque<String> = ArrayDeque()
    private lateinit var currentDirectoryEntry: String

    private val _directoryChildren = MutableLiveData<List<DataToTransfer>>(null)
    val directoryChildren: LiveData<List<DataToTransfer>>
        get() = _directoryChildren

    private val _directoryNavigationList = MutableLiveData<List<String>>()
    val directoryNavigationList: LiveData<List<String>>
        get() = _directoryNavigationList
    private val directoryList: MutableList<String> = mutableListOf()

    fun moveToPreviousDirectory() {
        val previousDirectoryEntry = directoryStack.pop()
        previousDirectoryEntry?.let {
            currentDirectoryEntry = it
            getDirectoryChildren(it)
        }
        directoryList.removeLast()
        _directoryNavigationList.value = directoryList
    }


    fun moveToDirectory(path: String) {
        // push previous directory with it's last visible item position into the directory stack
        directoryStack.push(
            currentDirectoryEntry
        )
        // change the current directory to point at the new path
        currentDirectoryEntry = path
        getDirectoryChildren(path)
        directoryList.add(path)
        _directoryNavigationList.value = directoryList
    }



    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentDirectoryEntry = filesRepository.getRootDirectory().path
            getDirectoryChildren(currentDirectoryEntry)
            directoryList.add(currentDirectoryEntry)
            withContext(Dispatchers.Main) {
                _directoryNavigationList.value = directoryList
            }
        }
    }


    fun getDirectoryChildren(directoryPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val directoryChildren = filesRepository.getDirectoryChildren(
                directoryPath
            )
            withContext(Dispatchers.Main) {
                _directoryChildren.value = directoryChildren
            }
        }
    }

    fun clearCurrentFolderChildren() {
        _directoryChildren.value = listOf()
    }
}