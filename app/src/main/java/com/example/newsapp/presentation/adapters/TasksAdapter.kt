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
import com.example.newsapp.utils.ColorPicker

class TasksAdapter : ListAdapter<TasksEntity, TasksAdapter.TaskViewHolder>(TasksDiffCallback()) {

    private val colorPicker = ColorPicker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wiki_task, parent, false)
        return TaskViewHolder(view, colorPicker)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, position)
    }

    class TaskViewHolder(itemView: View, private val colorPicker: ColorPicker) : RecyclerView.ViewHolder(itemView) {
        private val itemText: TextView = itemView.findViewById(R.id.goalTitle)
        private val taskDone: CheckBox = itemView.findViewById(R.id.task_done)
        private val dragIcon: ImageView = itemView.findViewById(R.id.drag_icon)
        private val background: View = itemView.findViewById(R.id.background_layout)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(task: TasksEntity, position: Int) {
            itemText.text = task.title
            taskDone.isChecked = task.isDone

            // Use colorPicker to set the background color
            background.setBackgroundColor(colorPicker.getColorAt(position))

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
