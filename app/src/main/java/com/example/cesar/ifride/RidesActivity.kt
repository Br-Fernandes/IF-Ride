package com.example.cesar.ifride

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.ContentValues.TAG

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle

import android.util.Log
import android.view.Gravity
import android.view.View

import android.widget.LinearLayout
import android.widget.TextView

import com.example.cesar.ifride.databinding.ActivityRidesBinding
import com.example.cesar.ifride.models.RideModel

import com.example.cesar.ifride.utils.CustomLayouts.Companion.dpToPx

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RidesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRidesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var chosenCity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRidesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        chosenCity = intent.getStringExtra("city").toString()

        putOneWayRides()

        binding.txtOneWayOption.setOnClickListener{
            putOneWayRides()
        }

        binding.txtReturnOption.setOnClickListener{
            putReturnRides()
        }
    }

    private fun putOneWayRides() {
        val queryOneWay = db.collection("Rides")
            .whereEqualTo("city", chosenCity)
            .whereEqualTo("direction", "Ida")

        val linearLayout = binding.llResults

        for (i in 0 until linearLayout.childCount){
            val childView = linearLayout.getChildAt(i)
            linearLayout.removeView(childView)
        }

        queryOneWay.get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val ride = document.toObject(RideModel::class.java)
                    linearLayout.addView(putRide(ride))

                }
            }
            .addOnFailureListener {exception ->
                Log.d(TAG, "deu ruim", exception)
            }

        personalizeBtn(binding.txtOneWayOption)
    }

    private fun putReturnRides() {
        val queryReturn = db.collection("Rides")
            .whereEqualTo("city", chosenCity)
            .whereEqualTo("direction", "Volta")

        val linearLayout = binding.llResults

        for (i in 0 until linearLayout.childCount){
            val childView = linearLayout.getChildAt(i)
            linearLayout.removeView(childView)
        }

        queryReturn.get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val ride  = document.toObject(RideModel::class.java)
                    linearLayout.addView(putRide(ride))
                }
            }.addOnFailureListener {exception ->
            Log.d(TAG, "deu ruim também", exception)
        }

        personalizeBtn(binding.txtReturnOption)
    }

    private fun putRide(ride: RideModel): LinearLayout {
        val hourRide = TextView(this)
        val driverRide = TextView(this)
        val newLinearLayout = LinearLayout(this)

        hourRide.apply {
            text = ride.dateHour.toString()
            textSize = 20f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        driverRide.apply {
            text = "Preço: \n${ride.price.toString()}"
            textSize = 25f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        newLinearLayout.apply {
            background = resources.getDrawable(R.drawable.border_rides)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(this@RidesActivity, 80f).toInt()
            ).apply {
                setMargins(dpToPx(this@RidesActivity, 15f).toInt(),
                        dpToPx(this@RidesActivity, 15f).toInt(),
                        dpToPx(this@RidesActivity, 15f).toInt(),
                        dpToPx(this@RidesActivity, 15f).toInt())
            }

            addView(hourRide)
            addView(driverRide)
        }

        onOpenRideInformatins(newLinearLayout)
        return newLinearLayout
    }

    private fun personalizeBtn(txtView: TextView) {
        val linearLayout = binding.llOptionsDirections
        val currentIndex = linearLayout.indexOfChild(txtView)
        val previousTextView: TextView? = linearLayout.getChildAt(currentIndex - 1) as? TextView
        val nextTextView: TextView? = linearLayout.getChildAt(currentIndex + 1) as? TextView

        txtView.setTextColor(resources.getColor(R.color.white_smoke))

        if (txtView.text == "Ida") {
            if (nextTextView != null) {
                txtView.setBackgroundResource(R.drawable.border_directions_left_selected)
                nextTextView.setBackgroundResource(R.drawable.border_directions_right)
                nextTextView.setTextColor(resources.getColor(R.color.solid_gray))
            }
        } else {
            if (previousTextView != null) {
                txtView.setBackgroundResource(R.drawable.border_directions_right_selected)
                previousTextView.setBackgroundResource(R.drawable.border_directions_left)
                previousTextView.setTextColor(resources.getColor(R.color.solid_gray))
            }
        }
    }

    private fun onOpenRideInformatins(rideLayout: LinearLayout) {
        rideLayout.setOnClickListener {
            val animator = ValueAnimator.ofInt(
                dpToPx(this,80f).toInt(),
                dpToPx(this,240f).toInt())
            animator.duration = 1000

            animator.addUpdateListener { animation ->
                val height =animation.animatedValue as Int
                rideLayout.layoutParams.height = height
                rideLayout.requestLayout()
            }
            animator.start()
        }
    }
}