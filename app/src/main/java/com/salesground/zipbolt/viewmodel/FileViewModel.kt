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


    companion object {
        const val ADDED_DIRECTORY = 1
        const val REMOVED_DIRECTORY = 2
        const val COMPLETED = 3
    }

    private val directoryStack: Deque<String> = ArrayDeque()
    private lateinit var currentDirectoryEntry: String

    private val _directoryChildren = MutableLiveData<List<DataToTransfer>>(null)
    val directoryChildren: LiveData<List<DataToTransfer>>
        get() = _directoryChildren


    private val _navigatedDirectory = MutableLiveData<Pair<String, Int>>(null)
    val navigatedDirectory: LiveData<Pair<String, Int>>
        get() = _navigatedDirectory

    private var _navigationHeaderText = ""
    val navigationHeaderText: String
        get() = _navigationHeaderText


    fun moveToPreviousDirectory() {
        val previousDirectoryEntry = directoryStack.pop()
        val navigatedDirectoryName = File(currentDirectoryEntry).name
        previousDirectoryEntry?.let {
            currentDirectoryEntry = it
            getDirectoryChildren(it)
        }
        _navigatedDirectory.value = Pair(navigatedDirectoryName, REMOVED_DIRECTORY)
    }


    fun moveToDirectory(path: String) {
        clearCurrentFolderChildren()
        // push previous directory with it's last visible item position into the directory stack
        directoryStack.push(
            currentDirectoryEntry
        )
        // change the current directory to point at the new path
        currentDirectoryEntry = path
        getDirectoryChildren(path)

        _navigatedDirectory.value = Pair(File(path).name, ADDED_DIRECTORY)
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentDirectoryEntry = filesRepository.getRootDirectory().path
            getDirectoryChildren(currentDirectoryEntry)
            withContext(Dispatchers.Main) {
                _navigatedDirectory.value = Pair(File(currentDirectoryEntry).name, ADDED_DIRECTORY)
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

    fun navigationResponded(modifiedPath: String) {
        _navigationHeaderText = modifiedPath
        _navigatedDirectory.value = Pair(_navigatedDirectory.value!!.first, COMPLETED)
    }
}