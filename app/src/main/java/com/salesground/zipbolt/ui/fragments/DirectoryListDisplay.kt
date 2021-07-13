package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.salesground.zipbolt.R

private const val DIRECTORY_PATH_ARG = "DirectoryPath"

class DirectoryListDisplay : Fragment() {
    private var directoryPath = ""
    companion object {
        @JvmStatic
        fun createNewInstance(directoryPath: String): DirectoryListDisplay {
            return DirectoryListDisplay().apply {
                arguments = Bundle().apply {
                    putString(DIRECTORY_PATH_ARG, directoryPath)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            directoryPath = it.getString(DIRECTORY_PATH_ARG) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_directory_list_display, container, false)
    }
}