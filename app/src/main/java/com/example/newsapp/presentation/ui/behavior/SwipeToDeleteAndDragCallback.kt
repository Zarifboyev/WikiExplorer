package com.example.newsapp.presentation.ui.behavior

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.presentation.adapters.TasksAdapter
import kotlin.math.abs

class SwipeToDeleteAndDragCallback(
    context: Context,
    private val adapter: TasksAdapter,
    private val onDelete: (Int) -> Unit
) : ItemTouchHelper.Callback() {

    private var swipeDistance: Float = 0f // Declare a variable to store swipe distance
    private val deleteIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
    private val intrinsicWidth: Int = deleteIcon.intrinsicWidth
    private val intrinsicHeight: Int = deleteIcon.intrinsicHeight
    // Adjust the swipe threshold percentage (default is 0.5f)
    private val swipeThreshold = 0.75f // Example: require the user to swipe 75% of the item's width to trigger delete


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.LEFT && abs(swipeDistance) > viewHolder.itemView.width * swipeThreshold) {
            onDelete(position)
        }
    }
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        swipeDistance = dX
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView


            // Draw the delete background

            // Draw the delete icon
            val iconMargin = (itemView.height - intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconLeft = itemView.right - iconMargin - intrinsicWidth
            val iconRight = itemView.right - iconMargin
            val iconBottom = iconTop + intrinsicHeight
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            deleteIcon.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}
