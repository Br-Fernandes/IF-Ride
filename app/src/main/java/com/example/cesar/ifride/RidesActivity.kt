package com.example.cesar.ifride

import android.animation.ValueAnimator
import android.content.ContentValues.TAG

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.example.cesar.ifride.databinding.ActivityRidesBinding
import com.example.cesar.ifride.models.RideModel

import com.example.cesar.ifride.utils.Util.Companion.dpToPx
import com.example.cesar.ifride.utils.Util.Companion.removeLinearLayoutChildren
import com.example.cesar.ifride.utils.Util.Companion.standardLinearLayout

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

        removeLinearLayoutChildren(linearLayout)

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

        removeLinearLayoutChildren(linearLayout)

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

        onOpenRideInformatins(newLinearLayout, ride)
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

        private fun onOpenRideInformatins(rideLayout: LinearLayout, ride: RideModel) {
            rideLayout.setOnClickListener {
                val animator = ValueAnimator.ofInt(
                    dpToPx(this,80f).toInt(),
                    dpToPx(this,240f).toInt())
                animator.duration = 1000

                animator.addUpdateListener { animation ->
                    val height = animation.animatedValue as Int
                    rideLayout.layoutParams.height = height
                    rideLayout.requestLayout()
                }
                animator.start()
                removeLinearLayoutChildren(rideLayout)
                putRideInformations(rideLayout, ride)
            }
        }

    private fun putRideInformations(rideLayout: LinearLayout, ride: RideModel) {
        rideLayout.orientation = LinearLayout.VERTICAL
        var closeBtn = setCloseBtn()
        var dateAndPrice =  setDateAndPrice(ride)
        /*var driverAndSeats = setDriverAndSeats()
        var confirmBtn = setConfirmBtn()*/
        rideLayout.addView(closeBtn)
        rideLayout.addView(dateAndPrice)
    }

    private fun setCloseBtn(): LinearLayout {
        var imageView = ImageView(this)
        var linearLayout = standardLinearLayout(this@RidesActivity)

        imageView.apply {
            setImageDrawable(ContextCompat.getDrawable(this@RidesActivity, R.drawable.close_x))
            background = resources.getDrawable(R.drawable.circle_border)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            foregroundGravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(this@RidesActivity, 16f).toInt(),
                dpToPx(this@RidesActivity, 16f).toInt()
            ).apply {
                setMargins(dpToPx(this@RidesActivity, 15f).toInt(),
                            dpToPx(this@RidesActivity, 15f).toInt(),
                            dpToPx(this@RidesActivity, 15f).toInt(),
                            dpToPx(this@RidesActivity, 15f).toInt()
                )
            }
        }
        linearLayout.addView(imageView)

        return linearLayout
    }

    private fun setDateAndPrice(ride: RideModel): LinearLayout {
        var linearLayout = standardLinearLayout(this@RidesActivity)
        var dateHour = TextView(this)
        var price = TextView(this)

        dateHour.apply {
            text = ride.dateHour.toString()
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(this@RidesActivity, 16f).toInt(),
                dpToPx(this@RidesActivity, 16f).toInt()
            ).apply {
                setMargins(dpToPx(this@RidesActivity, 15f).toInt(),
                    dpToPx(this@RidesActivity, 15f).toInt(),
                    dpToPx(this@RidesActivity, 15f).toInt(),
                    dpToPx(this@RidesActivity, 15f).toInt()
                )
            }
        }

        price.apply {
            text = ride.price.toString()
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(this@RidesActivity, 16f).toInt(),
                dpToPx(this@RidesActivity, 16f).toInt()
            ).apply {
                setMargins(dpToPx(this@RidesActivity, 15f).toInt(),
                    dpToPx(this@RidesActivity, 15f).toInt(),
                    dpToPx(this@RidesActivity, 15f).toInt(),
                    dpToPx(this@RidesActivity, 15f).toInt()
                )
            }
        }
        linearLayout.addView(dateHour)
        linearLayout.addView(price)

        return linearLayout
    }
    private fun setDriverAndSeats() {
        TODO("Not yet implemented")
    }

    private fun setConfirmBtn() {

    }

}

