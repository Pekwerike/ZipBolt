package com.salesground.zipbolt.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.salesground.zipbolt.MainActivity
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.FragmentFilesBinding
import com.salesground.zipbolt.viewmodel.FileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FilesFragment : Fragment() {
    private var mainActivity: MainActivity? = null

    companion object {
        var backStackCount: Int = 0
    }

    private val fileViewModel: FileViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModelLiveData()
        activity?.let {
            mainActivity = it as MainActivity
            mainActivity?.setBackButtonPressedClickListener(object :
                MainActivity.PopBackStackListener {
                override fun popStack() {
                    childFragmentManager.popBackStack()
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val filesFragmentLayout = FragmentFilesBinding.inflate(inflater, container, false)
        fileViewModel.getRootDirectory()
        return filesFragmentLayout.root
    }

    fun onDirectoryClicked(directoryPath: String) {
        backStackCount++
        childFragmentManager.run {
            commit {
                replace(
                    R.id.fragment_files_fragment_container,
                    DirectoryListDisplay.createNewInstance(directoryPath)
                )
                addToBackStack("kiki")
            }
        }
    }

    private fun observeViewModelLiveData() {
        fileViewModel.rootDirectory.observe(this) {
            it?.let {
                childFragmentManager.run {
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
}