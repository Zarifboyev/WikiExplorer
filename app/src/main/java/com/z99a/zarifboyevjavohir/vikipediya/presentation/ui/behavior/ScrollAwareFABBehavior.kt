package com.z99a.zarifboyevjavohir.vikipediya.presentation.ui.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.z99a.zarifboyevjavohir.R

class ScrollAwareFABBehavior(context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<ExtendedFloatingActionButton>(context, attrs) {

    private var isAtTop = true

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    val context = context
    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        if (target is RecyclerView) {
            if (dyConsumed > 0 && child.visibility == View.VISIBLE && isAtTop) {
                // User scrolled down, change to "Top" button
                isAtTop = false
                child.text = "Yuqoriga"
                child.setIconResource(R.drawable.ic_support)
            } else if (dyConsumed < 0 && !isAtTop) {
                // User scrolled up, show the map button
                val layoutManager = (target as RecyclerView).layoutManager
                if (layoutManager != null && layoutManager.findViewByPosition(0)?.top == 0) {
                    isAtTop = true
                    child.text = "Xaritalar"
                    child.setIconResource(R.drawable.ic_map_24dp)
                }
            }
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: ExtendedFloatingActionButton,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        setupFab(child)
        return true
    }

    private fun setupFab(fab: ExtendedFloatingActionButton) {
        fab.setOnClickListener {
            if (!isAtTop) {
                val recyclerView = (fab.parent as View).findViewById<RecyclerView>(R.id.places_list)
                recyclerView?.smoothScrollToPosition(0)
            }
        }
    }
}
