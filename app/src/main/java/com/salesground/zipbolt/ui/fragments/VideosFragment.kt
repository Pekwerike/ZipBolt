package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentVideosBinding
import com.salesground.zipbolt.viewmodel.VideoViewModel

class VideosFragment : Fragment() {
    private lateinit var fragmentVideosBinding: FragmentVideosBinding

    private val videoViewModel: VideoViewModel by activityViewModels<VideoViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentVideosLayout = FragmentVideosBinding.inflate(inflater, container, false)
        return fragmentVideosLayout.root

    }
}