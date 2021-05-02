package com.salesground.zipbolt

import android.app.Activity
import android.content.Context
import android.view.ViewStub
import androidx.databinding.DataBindingUtil
import com.salesground.zipbolt.databinding.CollapsedConnectedToPeerNoActionBinding
import com.salesground.zipbolt.databinding.CollapsedSearchingForPeersInformationBinding
import com.salesground.zipbolt.databinding.ExpandedConnectedToPeerNoActionBinding
import com.salesground.zipbolt.databinding.ExpandedSearchingForPeersInformationBinding


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

    fun getExpandedConnectedToPeerNoActionBinding(activity: Activity)
            : ExpandedConnectedToPeerNoActionBinding {
        val expandedConnectedToPeerNoActionView =
            activity.findViewById<ViewStub>(R.id.expanded_connected_to_peer_no_action_view_stub)
                .inflate()
        DataBindingUtil.bind<ExpandedConnectedToPeerNoActionBinding>(
            expandedConnectedToPeerNoActionView
        )
        return DataBindingUtil.getBinding(expandedConnectedToPeerNoActionView)!!
    }

    fun getCollapsedConnectedToPeerNoActionBinding(activity: Activity): CollapsedConnectedToPeerNoActionBinding {
        val collapsedConnectedToPeerNoActionView =
            activity.findViewById<ViewStub>(R.id.collapsed_connected_to_peer_no_action_view_stub)
                .inflate()
        DataBindingUtil.bind<CollapsedConnectedToPeerNoActionBinding>(
            collapsedConnectedToPeerNoActionView
        )
        return DataBindingUtil.getBinding(collapsedConnectedToPeerNoActionView)!!
    }

}