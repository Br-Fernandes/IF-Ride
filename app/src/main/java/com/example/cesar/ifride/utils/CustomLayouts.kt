package com.example.cesar.ifride.utils

import android.content.Context

class CustomLayouts {

    companion object {
        fun dpToPx(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }
    }
}
