package com.salesground.zipbolt.ui.screen.allmedia

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.salesground.zipbolt.databinding.AllMediaOnDeviceBinding
import com.salesground.zipbolt.ui.AllMediaOnDeviceFragmentsAdapter

@Composable
fun AllMediaOnDevice(
    supportFragmentManager: FragmentManager,
    viewPagerAdapterLifecycle: Lifecycle
) {
    AndroidViewBinding(AllMediaOnDeviceBinding::inflate) {
        //allMediaOnDeviceViewPager.isUserInputEnabled = false
        allMediaOnDeviceViewPager.adapter = AllMediaOnDeviceFragmentsAdapter(supportFragmentManager,
            viewPagerAdapterLifecycle)
        TabLayoutMediator(
            allMediaOnDeviceTabLayout,
            allMediaOnDeviceViewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "Apps"
                1 -> tab.text = "Images"
                2 -> tab.text = "Videos"
                3 -> tab.text = "Music"
                4 -> tab.text = "Files"
            }
        }.attach()
    }
}