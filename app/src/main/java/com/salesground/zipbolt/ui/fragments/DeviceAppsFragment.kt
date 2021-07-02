package com.salesground.zipbolt.ui.fragments

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.broadcast.SendDataBroadcastReceiver
import com.salesground.zipbolt.databinding.FragmentAppBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.applicationFragment.ApplicationFragmentAppsDisplayRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.ApplicationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeviceAppsFragment : Fragment() {
    private val applicationsViewModel: ApplicationsViewModel by activityViewModels()
    private lateinit var applicationFragmentAppsDisplayRecyclerViewAdapter: ApplicationFragmentAppsDisplayRecyclerViewAdapter
    private lateinit var fragmentAppBinding: FragmentAppBinding
    private var spanCount: Int = 3
    private var mainActivity: MainActivity? = null

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val sendDataBroadcastReceiver = SendDataBroadcastReceiver(
        object : SendDataBroadcastReceiver.SendDataButtonClickedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun sendDataButtonClicked() {
                applicationsViewModel.clearCollectionOfSelectedApps()
                applicationFragmentAppsDisplayRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            mainActivity = it as MainActivity
        }

        applicationFragmentAppsDisplayRecyclerViewAdapter =
            ApplicationFragmentAppsDisplayRecyclerViewAdapter(
                DataToTransferRecyclerViewItemClickListener {
                    if (applicationsViewModel.selectedApplications.contains(it)) {
                        mainActivity?.removeFromDataToTransferList(it)
                    } else {
                        mainActivity?.addToDataToTransferList(it)
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

    override fun onStart() {
        super.onStart()
        localBroadcastManager.registerReceiver(sendDataBroadcastReceiver,
            IntentFilter().apply {
                addAction(SendDataBroadcastReceiver.ACTION_SEND_DATA_BUTTON_CLICKED)
            }
        )
    }

    override fun onStop() {
        super.onStop()
        localBroadcastManager.unregisterReceiver(sendDataBroadcastReceiver)
    }
}