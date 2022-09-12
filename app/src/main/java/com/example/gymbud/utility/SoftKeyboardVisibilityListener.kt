package com.example.gymbud.utility

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver


class SoftKeyboardVisibilityListener(private val view: View, private val onKeyboardVisibilityChanged: (Boolean) -> Unit): ViewTreeObserver.OnGlobalLayoutListener {
    private var alreadyVisible = false
    private val defaultKeyboardHeightDP = 100
    private val estimatedKeyboardDP = defaultKeyboardHeightDP + 48
    private val rect: Rect = Rect()

    override fun onGlobalLayout() {
        val estimatedKeyboardHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            estimatedKeyboardDP.toFloat(),
            view.resources.displayMetrics
        ).toInt()

        view.getWindowVisibleDisplayFrame(rect)

        val heightDiff: Int = view.rootView.height - (rect.bottom - rect.top)
        val isVisible = heightDiff >= estimatedKeyboardHeight
        if (isVisible == alreadyVisible) {
            return
        }

        alreadyVisible = isVisible
        onKeyboardVisibilityChanged(isVisible)
    }
}