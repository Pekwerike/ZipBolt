package com.salesground.zipbolt.ui.fragments

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.broadcast.SendDataBroadcastReceiver
import com.salesground.zipbolt.databinding.FragmentVideosBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.VideoRecyclerViewCustomDivider
import com.salesground.zipbolt.ui.recyclerview.videoFragment.VideoFragmentRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.VideoViewModel
import javax.inject.Inject

class VideosFragment : Fragment() {
    private lateinit var fragmentVideosBinding: FragmentVideosBinding
    private lateinit var videoFragmentRecyclerViewAdapter: VideoFragmentRecyclerViewAdapter
    private var mainActivity: MainActivity? = null

    private val videoViewModel: VideoViewModel by activityViewModels()

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val sendDataBroadcastReceiver = SendDataBroadcastReceiver(
        object : SendDataBroadcastReceiver.SendDataButtonClickedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun sendDataButtonClicked() {
                videoViewModel.clearCollectionOfSelectedVideos()
                videoFragmentRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            mainActivity = it as MainActivity
        }

        videoFragmentRecyclerViewAdapter = VideoFragmentRecyclerViewAdapter(
            DataToTransferRecyclerViewItemClickListener {
                if (videoViewModel.selectedVideosForTransfer.contains(it)) {
                    mainActivity?.removeFromDataToTransferList(it)
                } else {
                    mainActivity?.addToDataToTransferList(it)
                }
            },
            videoViewModel.selectedVideosForTransfer
        )

        observeVideoViewModelLiveData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentVideosBinding = FragmentVideosBinding.inflate(inflater, container, false)
        return fragmentVideosBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentVideosBinding.run {
            fragmentVideosRecyclerview.run {
                adapter = videoFragmentRecyclerViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    VideoRecyclerViewCustomDivider(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    private fun observeVideoViewModelLiveData() {
        videoViewModel.allVideosOnDevice.observe(this) {
            videoFragmentRecyclerViewAdapter.submitList(it)
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