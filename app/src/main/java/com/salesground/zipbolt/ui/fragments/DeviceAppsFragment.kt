package com.salesground.zipbolt.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentAppBinding
import com.salesground.zipbolt.ui.recyclerview.applicationFragment.ApplicationFragmentAppsDisplayRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.ApplicationsViewModel


class DeviceAppsFragment : Fragment() {
    private val applicationsViewModel: ApplicationsViewModel by activityViewModels()
    private lateinit var applicationFragmentAppsDisplayRecyclerViewAdapter: ApplicationFragmentAppsDisplayRecyclerViewAdapter
    private lateinit var fragmentAppBinding: FragmentAppBinding
    private var spanCount: Int = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationFragmentAppsDisplayRecyclerViewAdapter =
            ApplicationFragmentAppsDisplayRecyclerViewAdapter()
        spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (resources.displayMetrics.density > 3.1 || resources.configuration.densityDpi < 245) {
                    2
                } else {
                    3
                }
            }
            else -> {
                if (resources.displayMetrics.density > 3.1 || resources.configuration.densityDpi < 245) {
                    4
                } else {
                    6
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentAppBinding = FragmentAppBinding.inflate(inflater, container, false)
        return fragmentAppBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(fragmentAppBinding) {
            fragmentAppDeviceApplicationsDisplayRecyclerview.adapter =
                applicationFragmentAppsDisplayRecyclerViewAdapter
            fragmentAppDeviceApplicationsDisplayRecyclerview.layoutManager = GridLayoutManager(
                requireContext(),
                spanCount
            )
        }
        observeViewModelLiveData()
    }

    private fun observeViewModelLiveData() {
        with(applicationsViewModel) {
            allApplicationsOnDevice.observe(viewLifecycleOwner) {
                it?.let {
                    applicationFragmentAppsDisplayRecyclerViewAdapter.submitList(it)
                }
            }
        }
    }
}