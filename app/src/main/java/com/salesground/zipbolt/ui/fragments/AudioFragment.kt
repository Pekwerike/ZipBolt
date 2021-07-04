package com.salesground.zipbolt.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.broadcast.SendDataBroadcastReceiver
import com.salesground.zipbolt.databinding.FragmentAudioBinding
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.audioFragment.AudioFragmentRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.AudioViewModel
import javax.inject.Inject


class AudioFragment : Fragment() {
    private lateinit var fragmentAudioBinding: FragmentAudioBinding
    private lateinit var audioFragmentRecyclerViewAdapter: AudioFragmentRecyclerViewAdapter
    private var mainActivity: MainActivity? = null
    private val audioViewModel: AudioViewModel by activityViewModels()

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val sendDataBroadcastReceiver = SendDataBroadcastReceiver(
        object : SendDataBroadcastReceiver.SendDataButtonClickedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun sendDataButtonClicked() {

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

            },
            audioViewModel.
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentAudioBinding = FragmentAudioBinding.inflate(inflater, container, false)
        return fragmentAudioBinding.root

    }
}