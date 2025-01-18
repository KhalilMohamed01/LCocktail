package com.example.lcocktail.Decorations

import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.R

class CenterScaleDecoration(
    private val scaleFactor: Float = 1.3f, // Bigger scale for the center item
    private val minScale: Float = 0.8f // Smaller scale for side items
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val centerX = parent.width / 2f
        val childCount = parent.childCount

        Log.d("CenterScaleDecoration", "Center X: $centerX, Child count: $childCount")

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            // Calculate the child's center
            val childCenterX = (child.left + child.right) / 2f
            val distanceFromCenter = Math.abs(centerX - childCenterX)

            // Calculate the scale
            val scale = (1 - distanceFromCenter / centerX).coerceIn(minScale, scaleFactor)

            // Apply scaling only to the view's transformation (not layout size)
            child.pivotX = child.width / 2f
            child.pivotY = child.height / 2f
            child.scaleX = scale
            child.scaleY = scale

            // Debugging scale
            Log.d(
                "CenterScaleDecoration",
                "Child $i - Center X: $childCenterX, Distance: $distanceFromCenter, Scale: $scale"
            )

            // Find the TextView
            val nameTextView = child.findViewById<TextView>(R.id.cocktailNameTextView)

            if (nameTextView != null) {
                if (distanceFromCenter < centerX / 3) { // Close to the center
                    // Center item: Show text and enlarge it
                    nameTextView.visibility = View.VISIBLE
                    nameTextView.textSize = 20f
                    Log.d("CenterScaleDecoration", "Child $i - Showing text: ${nameTextView.text}")
                } else {
                    // Non-center items: Hide text
                    nameTextView.visibility = View.GONE
                    Log.d("CenterScaleDecoration", "Child $i - Hiding text")
                }
            } else {
                Log.e("CenterScaleDecoration", "Child $i - TextView not found!")
            }
        }
    }
}
