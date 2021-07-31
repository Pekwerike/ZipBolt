package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentFilesBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import com.salesground.zipbolt.ui.recyclerview.filesFragment.DirectoryListDisplayRecyclerViewAdapter
import com.salesground.zipbolt.viewmodel.FileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class FilesFragment : Fragment() {
    private lateinit var directoryListDisplayRecyclerViewAdapter: DirectoryListDisplayRecyclerViewAdapter
    private lateinit var fragmentFilesBinding: FragmentFilesBinding
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
    private var mainActivity: MainActivity? = null

    companion object {
        var backStackCount: Int = 0
    }

    private val fileViewModel: FileViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            mainActivity = it as MainActivity
            mainActivity?.setBackButtonPressedClickListener(object :
                MainActivity.PopBackStackListener {
                override fun popStack(): Boolean {
                    backStackCount--
                    fileViewModel.moveToPreviousDirectory()
                    return true
                }
            })
        }

        directoryListDisplayRecyclerViewAdapter =
            DirectoryListDisplayRecyclerViewAdapter(requireContext(),
                DataToTransferRecyclerViewItemClickListener {
                    if (fileViewModel.selectedFilesForTransfer.contains(it)) {
                        fileViewModel.selectedFilesForTransfer.remove(it)
                        mainActivity?.removeFromDataToTransferList(it)
                    } else {
                        fileViewModel.selectedFilesForTransfer.add(it)
                        mainActivity?.addToDataToTransferList(it)
                    }
                }, DataToTransferRecyclerViewItemClickListener {
                    backStackCount++
                    fileViewModel.moveToDirectory(it)
                }, fileViewModel.selectedFilesForTransfer
            )
        recyclerViewLayoutManager = LinearLayoutManager(requireContext())
        observeViewModelLiveData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentFilesBinding = FragmentFilesBinding.inflate(inflater, container, false)
        return fragmentFilesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentFilesBinding.run {
            filesFragmentRecyclerView.run {
                layoutManager = recyclerViewLayoutManager
                adapter = directoryListDisplayRecyclerViewAdapter
            }
            filesFragmentDirectoryNavigationHeader.text = fileViewModel.navigationHeaderText
        }
    }


    private fun observeViewModelLiveData() {
        fileViewModel.directoryChildren.observe(this) {
            it?.let {
                directoryListDisplayRecyclerViewAdapter.submitList(it)
            }
        }
        fileViewModel.navigatedDirectory.observe(this) {
            it?.let {
                fragmentFilesBinding.run {
                    filesFragmentDirectoryNavigationHeader.run {
                        var currentHeaderText = fileViewModel.navigationHeaderText
                        when (it.second) {
                            FileViewModel.ADDED_DIRECTORY -> {
                                fileViewModel.navigationResponded(
                                    getString(
                                        R.string.directory_navigation_header_text,
                                        currentHeaderText, it.first
                                    )
                                )

                                text = fileViewModel.navigationHeaderText
                                fragmentFilesNavigationHeaderScrollView.post {
                                    fragmentFilesNavigationHeaderScrollView.smoothScrollTo(
                                        fragmentFilesNavigationHeaderScrollView
                                            .getChildAt(0).width, 0
                                    )
                                }

                            }
                            FileViewModel.COMPLETED -> {
                                // do nothing
                            }
                            else -> {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    currentHeaderText =
                                        currentHeaderText.removeRange(
                                            currentHeaderText.length - (it.first.length + 3),
                                            currentHeaderText.length
                                        )
                                    withContext(Dispatchers.Main) {
                                        fileViewModel.navigationResponded(currentHeaderText)
                                        text = currentHeaderText
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}