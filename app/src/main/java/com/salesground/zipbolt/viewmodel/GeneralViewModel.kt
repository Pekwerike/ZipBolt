package com.salesground.zipbolt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.salesground.zipbolt.utils.Event
import com.salesground.zipbolt.utils.SingleLiveDataEventForUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class GeneralViewModel @Inject constructor() : ViewModel() {

    private val _hasPermissionToFetchMedia = MutableLiveData<SingleLiveDataEventForUIState<Boolean>>()
    val hasPermissionToFetchMedia : LiveData<SingleLiveDataEventForUIState<Boolean>>
    get() = _hasPermissionToFetchMedia

    fun hasPermissionToFetchMedia(hasPermission: Boolean){
        _hasPermissionToFetchMedia.value = SingleLiveDataEventForUIState(hasPermission)
    }
}
