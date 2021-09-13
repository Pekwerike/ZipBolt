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
import com.salesground.zipbolt.viewmodel.DataToTransferViewModel
import com.salesground.zipbolt.viewmodel.GeneralViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeviceAppsFragment : Fragment() {
    private val applicationsViewModel: ApplicationsViewModel by activityViewModels()
    private val dataToTransferViewModel: DataToTransferViewModel by activityViewModels()
    private val generalViewModel: GeneralViewModel by activityViewModels()
    private lateinit var applicationFragmentAppsDisplayRecyclerViewAdapter: ApplicationFragmentAppsDisplayRecyclerViewAdapter
    private lateinit var applicationFragmentAppsDisplayLayoutManager: GridLayoutManager
    private lateinit var fragmentAppBinding: FragmentAppBinding
    private var spanCount: Int = 0
    private var mainActivity: MainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            mainActivity = it as MainActivity
        }

        applicationFragmentAppsDisplayRecyclerViewAdapter =
            ApplicationFragmentAppsDisplayRecyclerViewAdapter(
                DataToTransferRecyclerViewItemClickListener {
                    if (dataToTransferViewModel.collectionOfDataToTransfer.contains(it)) {
                        mainActivity?.removeFromDataToTransferList(it)
                    } else {
                        mainActivity?.addToDataToTransferList(it)
                    }
                },
                dataToTransferViewModel.collectionOfDataToTransfer
            )

        spanCount = getSpanCount()
        applicationFragmentAppsDisplayLayoutManager = GridLayoutManager(
            requireContext(),
            spanCount
        )
        observeViewModelLiveData()
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
            fragmentAppDeviceApplicationsDisplayRecyclerview.layoutManager =
                applicationFragmentAppsDisplayLayoutManager
        }
    }

    private fun observeViewModelLiveData() {
        applicationsViewModel.allApplicationsOnDevice.observe(this@DeviceAppsFragment) {
            it?.let {
                applicationFragmentAppsDisplayRecyclerViewAdapter.submitList(it)
            }
        }
        dataToTransferViewModel.dropAllSelectedItem.observe(this) {
            it.getEvent(javaClass.name)?.let {
                applicationFragmentAppsDisplayRecyclerViewAdapter.selectedApplications =
                    dataToTransferViewModel.collectionOfDataToTransfer
                /*applicationsViewModel.clickedApplicationSet.forEach { selectedApplicationIndex ->
                    applicationFragmentAppsDisplayRecyclerViewAdapter.notifyItemChanged(
                        selectedApplicationIndex
                    )
                }*/

                applicationFragmentAppsDisplayRecyclerViewAdapter.notifyDataSetChanged()
                /* applicationFragmentAppsDisplayRecyclerViewAdapter.notifyItemRangeChanged(
                     applicationFragmentAppsDisplayLayoutManager.findFirstVisibleItemPosition(),
                     applicationFragmentAppsDisplayLayoutManager.findLastVisibleItemPosition() + 1
                 )*/
            }
        }

        generalViewModel.hasPermissionToFetchMedia.observe(this) {
            it?.let {
                it.getEvent(javaClass.name)?.let {
                    if(it) {
                        applicationsViewModel.getAllApplicationsOnDevice()
                    }
                }
            }
        }

    }

    private fun getSpanCount(): Int {
        return when (resources.configuration.orientation) {
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

}