package com.example.lcocktail.Decorations

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CenterZoomDecoration(
    private val scaleFactor: Float = 1.2f, // Scale factor for the center item
    private val minScale: Float = 0.8f // Minimum scale for side items
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: android.graphics.Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val centerX = parent.width / 2f
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val childCenterX = (child.left + child.right) / 2f
            val distanceFromCenter = Math.abs(centerX - childCenterX)
            val scale = minScale + (1 - distanceFromCenter / centerX) * (scaleFactor - minScale)

            child.scaleX = scale.coerceIn(minScale, scaleFactor)
            child.scaleY = scale.coerceIn(minScale, scaleFactor)
        }
    }
}
