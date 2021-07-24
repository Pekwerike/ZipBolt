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

data class DirectoryEntry(
    val directoryPath: String,
    val firstVisibleFilePosition: Int
)

@HiltViewModel
class FileViewModel @Inject constructor(
    private val filesRepository: FileRepository
) : ViewModel() {
    private val directoryStack: Deque<DirectoryEntry> = ArrayDeque()
    private lateinit var currentDirectoryEntry: DirectoryEntry

    fun moveToPreviousDirectory() {
        val previousDirectoryEntry = directoryStack.pop()
        previousDirectoryEntry?.let {
            currentDirectoryEntry = it
            getDirectoryChildren(it.directoryPath)
        }
    }

    fun getDirectoryFirstVisibleItemPosition(): Int {
        return currentDirectoryEntry.firstVisibleFilePosition
    }

    fun moveToDirectory(path: String, positionOfClickedDirectory: Int) {

        // push previous directory with it's last visible item position into the directory stack
        directoryStack.push(
            DirectoryEntry(
                currentDirectoryEntry.directoryPath,
                positionOfClickedDirectory
            )
        )
        // change the current directory to point at the new path
        currentDirectoryEntry = DirectoryEntry(path, 0)
        getDirectoryChildren(path)
    }

    private val _directoryChildren = MutableLiveData<List<DataToTransfer>>(null)
    val directoryChildren: LiveData<List<DataToTransfer>>
        get() = _directoryChildren


    init {
        viewModelScope.launch {
            currentDirectoryEntry = DirectoryEntry(filesRepository.getRootDirectory().path, 0)
            getDirectoryChildren(currentDirectoryEntry.directoryPath)
        }
    }


    fun getDirectoryChildren(directoryPath: String) {
        viewModelScope.launch {
            _directoryChildren.value = withContext(Dispatchers.IO) {
                filesRepository.getDirectoryChildren(
                    directoryPath
                )
            }!!
        }
    }

    fun clearCurrentFolderChildren() {
        _directoryChildren.value = listOf()
    }
}