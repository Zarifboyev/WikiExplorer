package com.example.newsapp.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.data.entity.TasksEntity
import com.example.newsapp.databinding.ScreenWikiTasksBinding
import com.example.newsapp.presentation.adapters.TasksAdapter
import com.example.newsapp.presentation.ui.behavior.SwipeToDeleteAndDragCallback
import com.example.newsapp.presentation.ui.components.dialogs.AddGoalDialog
import com.example.newsapp.presentation.viewModels.WikiTaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WikiTaskScreen : Fragment() {

    private val viewModel: WikiTaskViewModel by viewModels()
    private lateinit var binding: ScreenWikiTasksBinding
    private lateinit var adapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScreenWikiTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeGoals()
    }

    private fun setupRecyclerView() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())

        binding.list.setHasFixedSize(true)
        adapter = TasksAdapter()
        binding.list.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(
            SwipeToDeleteAndDragCallback(
                requireContext(),
                adapter,
                onDelete = { position -> deleteTask(adapter.currentList[position]) }
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.list)
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun observeGoals() {
        viewModel.allGoals.observe(viewLifecycleOwner) { goals ->
            val sortedGoals = goals.sortedByDescending { it.timestamp }
            adapter.submitList(sortedGoals)
        }
    }

    private fun showAddGoalDialog() {
        val dialog = AddGoalDialog { goal ->
            viewModel.insert(goal)
        }
        dialog.show(childFragmentManager, "AddGoalDialog")
    }

    private fun showEditDialog(task: TasksEntity) {
        val dialog = AddGoalDialog { goal ->
            viewModel.update(goal)
        }
        dialog.show(childFragmentManager, "EditGoalDialog")
    }

    private fun deleteTask(task: TasksEntity) {
        viewModel.delete(task)
    }
}
