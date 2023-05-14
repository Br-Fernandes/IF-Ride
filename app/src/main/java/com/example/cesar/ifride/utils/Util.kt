package com.example.cesar.ifride.utils

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import java.text.SimpleDateFormat
import java.util.*

class Util {

    companion object {
        fun dpToPx(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

        fun removeLinearLayoutChildren(linearLayout: LinearLayout) {
            while (linearLayout.childCount != 0) {
                val childView = linearLayout.getChildAt(0)
                linearLayout.removeView(childView)
            }
        }

        fun standardLinearLayout(context: Context): LinearLayout {
            val linearLayout = LinearLayout(context)
            linearLayout.apply {
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(context, 80f).toInt(),
                    1f
                )
            }
            return linearLayout
        }

        fun formatDate(date: Date): String {
            val dayFormat = SimpleDateFormat("EEEE dd/MM", Locale.getDefault())
            val timeFormat = SimpleDateFormat("'às' HH:mm", Locale.getDefault())

            val dayString = dayFormat.format(date)
            val timeString = timeFormat.format(date)

            return "$dayString\n$timeString"
        }

    }


}