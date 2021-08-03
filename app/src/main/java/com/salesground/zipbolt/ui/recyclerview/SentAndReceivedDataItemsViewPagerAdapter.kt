package com.salesground.zipbolt.ui.recyclerview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.salesground.zipbolt.ui.fragments.ReceivedDataFragment
import com.salesground.zipbolt.ui.fragments.SentDataFragment

class SentAndReceiveDataItemsViewPagerAdapter(
    private val fragmentManager: FragmentManager, lifecycle: Lifecycle,
    private val isSender: Boolean
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }


    override fun createFragment(position: Int): Fragment {
        return when {
            isSender && position == 0 -> {
                SentDataFragment()
            }
            isSender && position == 1 -> {
                ReceivedDataFragment()
            }
            !isSender && position == 0 -> {
                ReceivedDataFragment()
            }
            else -> {
                SentDataFragment()
            }
        }
    }
}