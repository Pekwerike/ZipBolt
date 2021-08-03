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
import com.salesground.zipbolt.databinding.FragmentAudioBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.HalfLineRecyclerViewCustomDivider
import com.salesground.zipbolt.ui.recyclerview.audioFragment.AudioFragmentRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.AudioViewModel
import com.salesground.zipbolt.viewmodel.DataToTransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioFragment : Fragment() {
    private lateinit var fragmentAudioBinding: FragmentAudioBinding
    private lateinit var audioFragmentRecyclerViewAdapter: AudioFragmentRecyclerViewAdapter
    private var mainActivity: MainActivity? = null
    private val audioViewModel: AudioViewModel by activityViewModels()
    private val dataToTransferViewModel: DataToTransferViewModel by activityViewModels()

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val sendDataBroadcastReceiver = SendDataBroadcastReceiver(
        object : SendDataBroadcastReceiver.SendDataButtonClickedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun sendDataButtonClicked() {
                audioFragmentRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            mainActivity = it as MainActivity
        }

        audioFragmentRecyclerViewAdapter = AudioFragmentRecyclerViewAdapter(
            DataToTransferRecyclerViewItemClickListener {
                if (dataToTransferViewModel.getCollectionOfDataToTransfer().contains(it)) {
                    mainActivity?.removeFromDataToTransferList(it)
                } else {
                    mainActivity?.addToDataToTransferList(it)
                }
            },
            dataToTransferViewModel.getCollectionOfDataToTransfer()
        )
        observeAudioViewModelLiveData()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentAudioBinding = FragmentAudioBinding.inflate(inflater, container, false)
        return fragmentAudioBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentAudioBinding.run {
            fragmentMusicRecyclerview.run {
                adapter = audioFragmentRecyclerViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    HalfLineRecyclerViewCustomDivider(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    private fun observeAudioViewModelLiveData() {
        audioViewModel.deviceAudio.observe(this) {
            audioFragmentRecyclerViewAdapter.submitList(it)
        }
    }

    override fun onStart() {
        super.onStart()
        localBroadcastManager.registerReceiver(
            sendDataBroadcastReceiver,
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