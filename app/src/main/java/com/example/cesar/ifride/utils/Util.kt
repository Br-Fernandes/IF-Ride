package com.example.cesar.ifride.utils

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.startActivity
import com.example.cesar.ifride.LoginActivity
import com.example.cesar.ifride.R
import com.example.cesar.ifride.RegisterDriverActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Util {
    companion object {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()

        fun dpToPx(context: Context?, dp: Float): Float {
            return dp * context!!.resources.displayMetrics.density
        }

        fun removeLinearLayoutChildren(linearLayout: LinearLayout) {
            while (linearLayout.childCount != 0) {
                val childView = linearLayout.getChildAt(0)
                linearLayout.removeView(childView)
            }
        }

        fun standardLinearLayout(context: Context?): LinearLayout {
            val linearLayout = LinearLayout(context)
            linearLayout.apply {
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(context!!, 100f).toInt(),
                    1f
                ).apply {
                    marginStart = dpToPx(context, 5f).toInt()
                    marginEnd = dpToPx(context, 5f).toInt()
                }
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

            return "$formatedDate\nàs $formatedHour"
        }

        fun linearLayoutAnimator(context: Context ,layout: LinearLayout) {
            val animator = ValueAnimator.ofInt(
                dpToPx(context!!, 100f).toInt(),
                dpToPx(context, 410f).toInt()
            )
            animator.duration = 0

            animator.addUpdateListener { animation ->
                val height = animation.animatedValue as Int
                layout.layoutParams.height = height
                layout.orientation = LinearLayout.VERTICAL
                layout.requestLayout()
            }
            animator.start()
        }

        fun verifyCurrentUser(): Boolean {
            return (auth.currentUser == null)
        }

        fun verifyIsDriver(callback: (Boolean) -> Unit) {
            val query = db.collection("Users").whereEqualTo("email", auth.currentUser!!.email)
            query.get().addOnSuccessListener { querySnapshot ->
                val value = querySnapshot.documents[0].get("isDriver").toString().toBoolean()

                if (!value) {
                    callback.invoke(false)
                } else {
                    callback.invoke(true)
                }
            }
        }

        fun phoneNumberFormatted(phoneNumber: String): String {
            val countryCode = phoneNumber.substring(0, 2)
            val firstBlock = phoneNumber.substring(2, 7)
            val secondBlock = phoneNumber.substring(7, 11)
            return "($countryCode) $firstBlock $secondBlock"
        }
    }
}