package com.example.newsapp.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.entity.TasksEntity

class TasksAdapter : ListAdapter<TasksEntity, TasksAdapter.TaskViewHolder>(TasksDiffCallback()) {

    // Define the array of colors
    private val colors = arrayOf(
        0xFF665E40.toInt()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wiki_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, position)
    }

    private fun getSequentialColor(position: Int): Int {
        return colors[position % colors.size]
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemText: TextView = itemView.findViewById(R.id.goalTitle)
        private val taskDone: CheckBox = itemView.findViewById(R.id.task_done)
        private val dragIcon: ImageView = itemView.findViewById(R.id.drag_icon)
        private val background: View = itemView.findViewById(R.id.background_layout)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(task: TasksEntity, position: Int) {
            itemText.text = task.title
            taskDone.isChecked = task.isDone

            // Use the getSequentialColor method to set the background color
            background.setBackgroundColor(getSequentialColor(position))

            taskDone.setOnCheckedChangeListener { _, isChecked ->
                task.isDone = isChecked
                // Update the task status in your data source if necessary
            }

            dragIcon.setOnTouchListener { _, _ ->
                // Handle drag event if necessary
                false
            }
        }
    }

    private class TasksDiffCallback : DiffUtil.ItemCallback<TasksEntity>() {
        override fun areItemsTheSame(oldItem: TasksEntity, newItem: TasksEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TasksEntity, newItem: TasksEntity): Boolean {
            return oldItem == newItem
        }
    }
}
