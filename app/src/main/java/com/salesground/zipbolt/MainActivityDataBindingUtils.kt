package com.salesground.zipbolt

import android.app.Activity
import android.view.ViewStub
import com.salesground.zipbolt.databinding.*


object MainActivityDataBindingUtils {


    fun getConnectedToPeerNoActionPersistentBottomSheetBinding(activity: Activity):
            ConnectedToPeerNoActionPersistentBottomSheetLayoutBinding {
        val view =
            activity.findViewById<ViewStub>(R.id.connected_to_peer_no_action_persistent_bottom_sheet_view_stub)
                .inflate()
        return ConnectedToPeerNoActionPersistentBottomSheetLayoutBinding.bind(view)
    }

    fun getConnectedToPeerTransferOngoingPersistentBottomSheetBinding(activity: Activity):
            ConnectedToPeerTransferOngoingPersistentBottomSheetBinding {
        val view =
            activity.findViewById<ViewStub>(R.id.connected_to_peer_transfer_ongoing_persistent_bottom_sheet_view_stub)
                .inflate()
        return ConnectedToPeerTransferOngoingPersistentBottomSheetBinding.bind(view)
    }


}