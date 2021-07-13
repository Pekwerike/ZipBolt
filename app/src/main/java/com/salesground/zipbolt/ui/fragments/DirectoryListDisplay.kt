package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.salesground.zipbolt.databinding.FragmentDirectoryListDisplayBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.directoryListDisplayFragment.DirectoryListDisplayRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.FileViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val DIRECTORY_PATH_ARG = "DirectoryPath"

@AndroidEntryPoint
class DirectoryListDisplay : Fragment() {
    private lateinit var fragmentDirectoryListDisplayBinding: FragmentDirectoryListDisplayBinding
    private lateinit var directoryListDisplayRecyclerViewAdapter: DirectoryListDisplayRecyclerViewAdapter
    private var filesFragment: FilesFragment? = null
    private val fileViewModel: FileViewModel by activityViewModels()
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
        observeViewModelLiveData()
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
        if (directoryPath.isNotEmpty()) {
            fileViewModel.clearCurrentFolderChildren()
            fileViewModel.getDirectoryChildren(directoryPath)
        }
        // Inflate the layout for this fragment
        return fragmentDirectoryListDisplayBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentDirectoryListDisplayBinding.run {
            fragmentDirectoryListDisplayRecyclerview.run {
                adapter = directoryListDisplayRecyclerViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun observeViewModelLiveData() {
        fileViewModel.directoryChildren.observe(this) {
            it?.let {
                directoryListDisplayRecyclerViewAdapter.submitList(it)
            }
        }
    }
}