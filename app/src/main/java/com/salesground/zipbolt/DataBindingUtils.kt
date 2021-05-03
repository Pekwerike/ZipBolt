package com.salesground.zipbolt

import android.app.Activity
import android.content.Context
import android.view.ViewStub
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import androidx.viewbinding.ViewBindings
import com.salesground.zipbolt.databinding.*


object DataBindingUtils {

    fun getExpandedSearchingForPeersBinding(activity: Activity)
            : ExpandedSearchingForPeersInformationBinding {
        val expandedSearchingForPeersInfoView =
            activity.findViewById<ViewStub>(R.id.expanded_searching_for_peers_info_view_stub)
                .inflate()
        DataBindingUtil.bind<ExpandedSearchingForPeersInformationBinding>(
            expandedSearchingForPeersInfoView
        )
        return DataBindingUtil.getBinding(expandedSearchingForPeersInfoView)!!
    }

    fun getCollapsedSearchingForPeersBinding(activity: Activity): CollapsedSearchingForPeersInformationBinding {
        val collapsedSearchingForPeersInfoView =
            activity.findViewById<ViewStub>(R.id.collapsed_searching_for_peers_info_view_stub)
                .inflate()
        DataBindingUtil.bind<CollapsedSearchingForPeersInformationBinding>(
            collapsedSearchingForPeersInfoView
        )
        return DataBindingUtil.getBinding(collapsedSearchingForPeersInfoView)!!
    }

    fun getConnectedToPeerNoActionPersistentBottomSheetBinding(activity: Activity):
            ConnectedToPeerNoActionPersistentBottomSheetLayoutBinding {
        val view =
            activity.findViewById<ViewStub>(R.id.connected_to_peer_no_action_persistent_bottom_sheet_view_stub)
                .inflate()
        return ConnectedToPeerNoActionPersistentBottomSheetLayoutBinding.bind(view)
    }

}