package com.salesground.zipbolt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.salesground.zipbolt.ui.fragments.*

class AllMediaOnDeviceViewPager2Adapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 5
    }


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DeviceAppsFragment()
            1 -> ImageFragment()
            2 -> VideosFragment()
            3 -> AudioFragment()
            4 -> FilesFragment()
            else -> ImageFragment()
        }
    }
}

class AllMediaOnDeviceViewPagerAdapter(private val fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(
        fragmentManager,
        FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DeviceAppsFragment()
            1 -> ImageFragment()
            2 -> VideosFragment()
            3 -> AudioFragment()
            4 -> FilesFragment()
            else -> ImageFragment()
        }
    }

    override fun getCount(): Int {
        return 5
    }
}

