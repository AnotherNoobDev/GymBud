@file:Suppress("DEPRECATION")
// todo ProgressDialog is deprecated

package com.gymbud.gymbud.ui

// TEST_DATA_GENERATION
// import com.gymbud.gymbud.utility.populateWithSessions
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gymbud.gymbud.BaseApplication
import com.gymbud.gymbud.databinding.FragmentTemplatesBinding
import com.gymbud.gymbud.model.Item
import com.gymbud.gymbud.model.ItemIdentifier
import com.gymbud.gymbud.model.ItemType
import com.gymbud.gymbud.ui.viewmodel.ItemViewModel
import com.gymbud.gymbud.ui.viewmodel.ItemViewModelFactory
import com.gymbud.gymbud.utility.distributeFile
import com.gymbud.gymbud.utility.saveProgramToFile
import com.gymbud.gymbud.utility.serializeProgramTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


const val TAG = "TemplatesFragment"


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

            exportProgramButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    openExportProgramSelectionDialog()
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.hasData().collect { withData ->
                    if (withData) {
                        loadDefaultsButton.visibility = View.GONE
                        exportProgramButton.visibility = View.VISIBLE
                    } else {
                        loadDefaultsButton.visibility = View.VISIBLE
                        exportProgramButton.visibility = View.GONE
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


    private suspend fun openExportProgramSelectionDialog() {
        // get a list of all programs on the device
        val programs = viewModel.getItemsByType(ItemType.PROGRAM_TEMPLATE).first()

        // populate dialog
        val programsByName = programs.map {it.name}
        var checkedItem = 0

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Program to export...")
            .setSingleChoiceItems(programsByName.toTypedArray(), checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton("Ok") { _, _ ->
                openExportProgramFilenameDialog(programs[checkedItem])
            }
            .setNegativeButton("Cancel") {_,_ ->
            }
            .show()
    }


    private fun openExportProgramFilenameDialog(program: Item) {
        val context = requireContext()

        val input = EditText(context)
        input.setText(program.name.replace(' ', '-'))

        MaterialAlertDialogBuilder(context)
            .setTitle("Export Program as...")
            .setView(input)
            .setPositiveButton("Ok") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    exportProgram(program.id,  input.text.toString())
                }
            }
            .setNegativeButton("Cancel") {_,_ ->
            }
            .show()
    }


    private suspend fun exportProgram(programTemplate: ItemIdentifier, filename: String) {
        // serialize selected program
        withContext(Dispatchers.IO) {
            val serialized = serializeProgramTemplate(programTemplate, (requireActivity().application as BaseApplication).database)

            // write serialized program to file
            val file = saveProgramToFile(requireContext(), filename, serialized)

            // send file via intent
            distributeProgram(file)

            // todo how to cleanup files??
            // no way to know when intent completes and we no longer need file
            // safe bet would be to clear all files on export or create some kind of temporary file??
        }
    }


    private fun distributeProgram(programFile: File) {
        val intent = distributeFile(programFile, requireContext(), "text/plain", "Sharing GymBud Program", "Share File")
        startActivity(intent)
    }
}