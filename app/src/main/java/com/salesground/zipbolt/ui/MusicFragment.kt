package com.salesground.zipbolt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentMusicBinding
import com.salesground.zipbolt.ui.theme.ZipBoltTheme


class MusicFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentMusicLayout = FragmentMusicBinding.inflate(inflater, container, false)
        return fragmentMusicLayout.root

    }
}