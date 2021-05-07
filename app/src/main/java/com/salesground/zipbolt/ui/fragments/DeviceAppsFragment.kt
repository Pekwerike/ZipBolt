package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentAppBinding


class DeviceAppsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val deviceAppsFragmentLayout = FragmentAppBinding.inflate(inflater, container, false)

        return deviceAppsFragmentLayout.root

    }

}