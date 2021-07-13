package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentFilesBinding
import com.salesground.zipbolt.viewmodel.FileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FilesFragment : Fragment() {

    private val fileViewModel: FileViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val filesFragmentLayout = FragmentFilesBinding.inflate(inflater, container, false)

        return filesFragmentLayout.root
    }

    fun onDirectoryClicked(directoryPath: String) {
        requireActivity().supportFragmentManager.run {
            commit {
                replace(
                    R.id.fragment_files_fragment_container,
                    DirectoryListDisplay.createNewInstance(directoryPath)
                )
                addToBackStack("kiki")
            }
        }
    }

    fun observeViewModelLiveData() {
        fileViewModel.rootDirectory.observe(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.run {
                commit {
                    replace(
                        R.id.fragment_files_fragment_container,
                        DirectoryListDisplay.createNewInstance(it.path)
                    )
                    addToBackStack("kiki")
                }
            }
        }
    }
}