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

        fun formatDate(dateString: String): String {
            val formateInput = SimpleDateFormat("dd-MM-yyyy-HH-mm", Locale.getDefault())
            val formateOutDate = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
            val formateSOutHour = SimpleDateFormat("HH:mm", Locale.getDefault())

            val data = formateInput.parse(dateString)

            val formatedDate = formateOutDate.format(data)
            val formatedHour = formateSOutHour.format(data)

            return "$formatedDate\n$formatedHour"
        }

    }


}