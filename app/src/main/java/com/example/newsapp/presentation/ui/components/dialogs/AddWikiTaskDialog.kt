package com.example.newsapp.presentation.ui.components.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.newsapp.R
import com.example.newsapp.data.entity.TasksEntity
import com.example.newsapp.databinding.DialogAddWikiTaskBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddGoalDialog(
    private val task: TasksEntity? = null,
    private val onSave: (TasksEntity) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogAddWikiTaskBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddWikiTaskBinding.inflate(LayoutInflater.from(requireContext()))

        // Use MaterialAlertDialogBuilder with custom theme overlay
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialAlertDialog)
            .setTitle("Add Task")
            .setView(binding.root)
            .setPositiveButton("Add") { dialog, _ ->
                val title = binding.goalTitleInput.text.toString()
                if (title.isNotEmpty()) {
                    val color = task?.color ?: getDefaultColor()
                    val timestamp = task?.timestamp ?: System.currentTimeMillis()
                    val newTask = task?.copy(title = title, color = color, timestamp = timestamp)
                        ?: TasksEntity(title = title, color = color, timestamp = timestamp)
                    onSave(newTask)
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Set task title if provided
        task?.let {
            binding.goalTitleInput.setText(it.title)
        }

        return builder.create()
    }

    private fun getDefaultColor(): Int {
        // Return a default color. You can customize this to return a specific color or a random color.
        return resources.getColor(R.color.md_theme_primary, null)
    }
}
