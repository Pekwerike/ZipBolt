package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentDirectoryListDisplayBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.DirectoryListDisplayRecyclerViewAdapter

private const val DIRECTORY_PATH_ARG = "DirectoryPath"

class DirectoryListDisplay : Fragment() {
    private lateinit var fragmentDirectoryListDisplayBinding: FragmentDirectoryListDisplayBinding
    private lateinit var directoryListDisplayRecyclerViewAdapter: DirectoryListDisplayRecyclerViewAdapter
    private var filesFragment: FilesFragment? = null

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
        parentFragment?.let {
            filesFragment = it as FilesFragment
        }
        directoryListDisplayRecyclerViewAdapter = DirectoryListDisplayRecyclerViewAdapter(
            requireContext(),
            DataToTransferRecyclerViewItemClickListener {
                it as DataToTransfer.DeviceFile
                if (it.file.isDirectory) {
                    filesFragment?.onDirectoryClicked(it.file.path)
                }
            }
        )
        arguments?.let {
            directoryPath = it.getString(DIRECTORY_PATH_ARG) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDirectoryListDisplayBinding = FragmentDirectoryListDisplayBinding.inflate(
            inflater,
            container,
            false
        )
        // Inflate the layout for this fragment
        return fragmentDirectoryListDisplayBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}