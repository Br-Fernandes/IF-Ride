package com.example.cesar.ifride

import android.animation.ValueAnimator
import android.content.ContentValues.TAG

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cesar.ifride.adapters.AdapterRide

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
    private lateinit var resultsRC: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRidesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        db = Firebase.firestore
        resultsRC = binding.rcResults

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
        resultsRC.removeAllViews()

        val ridesList: MutableList<RideModel> = mutableListOf()

        val queryOneWay = db.collection("Rides")
            .whereEqualTo("city", chosenCity)
            .whereEqualTo("direction", "Ida")

        queryOneWay.get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val ride = document.toObject(RideModel::class.java)
                    ridesList.add(ride)
                }
                initRecyclerView(ridesList)

            }
            .addOnFailureListener {exception ->
                Log.d(TAG, "deu ruim", exception)
            }
        personalizeBtn(binding.txtOneWayOption)
    }

    private fun putReturnRides() {
        resultsRC.removeAllViews()

       val ridesList: MutableList<RideModel> = mutableListOf()

        val queryReturn = db.collection("Rides")
            .whereEqualTo("city", chosenCity)
            .whereEqualTo("direction", "Volta")

        queryReturn.get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val ride  = document.toObject(RideModel::class.java)
                    ridesList.add(ride)
                }
                initRecyclerView(ridesList)

            }.addOnFailureListener {exception ->
            Log.d(TAG, "deu ruim também", exception)
        }
        personalizeBtn(binding.txtReturnOption)
    }

    private fun initRecyclerView(ridesList: MutableList<RideModel>) {
        resultsRC.layoutManager = LinearLayoutManager(this)
        resultsRC.setHasFixedSize(true)
        resultsRC.adapter = AdapterRide(this, ridesList)
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
}

