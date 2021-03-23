package com.salesground.zipbolt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AllMediaOnDeviceFragmentsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

       override fun getItemCount(): Int {
          return 5
       }

       override fun createFragment(position: Int): Fragment {
          return when(position){
               0 -> DeviceAppsFragment()
               1 -> TestImageFragment()
               2 -> VideosFragment()
               3 -> MusicFragment()
               4 -> FilesFragment()
              else -> DeviceAppsFragment()
          }
       }
}