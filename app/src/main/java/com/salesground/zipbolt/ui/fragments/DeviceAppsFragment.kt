package com.salesground.zipbolt.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.databinding.FragmentAppBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.applicationFragment.ApplicationFragmentAppsDisplayRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.ApplicationsViewModel


class DeviceAppsFragment : Fragment() {
    private val applicationsViewModel: ApplicationsViewModel by activityViewModels()
    private lateinit var applicationFragmentAppsDisplayRecyclerViewAdapter: ApplicationFragmentAppsDisplayRecyclerViewAdapter
    private lateinit var fragmentAppBinding: FragmentAppBinding
    private var spanCount: Int = 3
    private var mainActivity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            mainActivity = it as MainActivity
        }

        applicationFragmentAppsDisplayRecyclerViewAdapter =
            ApplicationFragmentAppsDisplayRecyclerViewAdapter(
                DataToTransferRecyclerViewItemClickListener {
                    if (applicationsViewModel.selectedApplications.contains(it)) {
                        mainActivity?.addToDataToTransferList(it)
                    } else {
                        mainActivity?.removeFromDataToTransferList(it)
                    }
                },
                applicationsViewModel.selectedApplications
            )
        spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (resources.displayMetrics.density > 3.1 || resources.configuration.densityDpi < 245) {
                    3
                } else {
                    4
                }
            }
            else -> {
                if (resources.displayMetrics.density > 3.1 || resources.configuration.densityDpi < 245) {
                    5
                } else {
                    7
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
            val deviceAppsFragmentGridLayoutManager = GridLayoutManager(requireContext(), spanCount)
            deviceAppsFragmentGridLayoutManager.spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (applicationFragmentAppsDisplayRecyclerViewAdapter.getItemViewType(
                            position
                        )) {
                            ApplicationFragmentAppsDisplayRecyclerViewAdapter.AdapterViewTypes.NORMAL.value -> 1
                            ApplicationFragmentAppsDisplayRecyclerViewAdapter.AdapterViewTypes.EXPANDED.value -> spanCount
                            else -> 1
                        }
                    }
                }
            fragmentAppDeviceApplicationsDisplayRecyclerview.layoutManager =
                deviceAppsFragmentGridLayoutManager
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