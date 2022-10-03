@file:Suppress("DEPRECATION")
// todo ProgressDialog is deprecated

package com.example.gymbud.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentTemplatesBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
// TEST_DATA_GENERATION
// import com.example.gymbud.utility.populateWithSessions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class TemplatesFragment : Fragment() {
    private var _binding: FragmentTemplatesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }

    private var populateWithDefaultsProgressDialog: ProgressDialog? = null

    // TEST_DATA_GENERATION only needed with test data generation
    // private var debugDataReady = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)

        binding.apply {
            libraryPanel.title.text = "Library"
            libraryPanel.description.text = "View, add and modify the Templates available on this device."
            libraryPanel.card.setOnClickListener {
                val action = TemplatesFragmentDirections.actionTemplatesFragmentToProgramBuilderFragment()
                findNavController().navigate(action)
            }

            loadDefaultsButton.setOnClickListener {
                val dialog = ProgressDialog(requireContext())
                dialog.max = 100
                dialog.setTitle("Loading default Programs...")
                dialog.setMessage("\n\nPlease do not close the app while this operation is in progress.")
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                dialog.show()
                populateWithDefaultsProgressDialog = dialog

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.populateWithDefaults {
                        updatePopulateWithDefaultsProgress(it)
                    }

                    // TEST_DATA_GENERATION generate test data (for debugging only!!)
                    /*
                    val app = activity?.application as BaseApplication
                    populateWithSessions(app.programRepository, app.sessionRepository)
                    debugDataReady = true
                    updatePopulateWithDefaultsProgress(100)
                     */
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.hasData().collect { withData ->
                    if (withData) {
                        loadDefaultsButton.visibility = View.GONE
                    } else {
                        loadDefaultsButton.visibility = View.VISIBLE
                    }
                }
            }
        }

        return binding.root
    }


    private fun updatePopulateWithDefaultsProgress(progress: Int) {
        populateWithDefaultsProgressDialog?.progress = progress

        // TEST_DATA_GENERATION && debugDataReady
        if (progress == 100) {
            activity?.runOnUiThread {
                populateWithDefaultsProgressDialog?.dismiss()
            }

            Toast.makeText(
                requireContext(), "Default Programs have been successfully loaded.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}