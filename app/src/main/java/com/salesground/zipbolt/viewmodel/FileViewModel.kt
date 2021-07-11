package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salesground.zipbolt.repository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FileViewModel(
    private val filesRepository: FileRepository
) : ViewModel() {

    private val _directoryChildren = MutableLiveData<Array<File>>(null)
    val directoryChildren: LiveData<Array<File>>
        get() = _directoryChildren


    fun getRootDirectory() {
        viewModelScope.launch {

        }
    }

    fun getFolderChildren(directoryPath: String) {
        viewModelScope.launch {
            _directoryChildren.value = withContext(Dispatchers.IO) {
                filesRepository.getDirectoryChildren(
                    directoryPath
                )
            }!!
        }
    }

    fun clearCurrentFolderChildren() {

    }
}