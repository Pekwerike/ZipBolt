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
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val filesRepository: FileRepository
) : ViewModel() {

    private val _directoryChildren = MutableLiveData<List<DataToTransfer>>(null)
    val directoryChildren: LiveData<List<DataToTransfer>>
        get() = _directoryChildren

    private val _rootDirectory = MutableLiveData<File>(null)
    val rootDirectory: LiveData<File>
        get() = _rootDirectory


    fun getRootDirectory() {
        viewModelScope.launch {
            _rootDirectory.value = withContext(Dispatchers.IO) {
                filesRepository.getRootDirectory()
            }!!
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