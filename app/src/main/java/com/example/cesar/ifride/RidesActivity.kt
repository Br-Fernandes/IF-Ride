package com.example.cesar.ifride

import android.content.ContentValues.TAG

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle

import android.util.Log

import android.widget.TextView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.cesar.ifride.adapters.AdapterRide
import com.example.cesar.ifride.databinding.ActivityRidesBinding
import com.example.cesar.ifride.models.RideModel
import com.example.cesar.ifride.utils.Util

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class RidesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRidesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var chosenCity: String
    private lateinit var resultsRC: RecyclerView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRidesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        resultsRC = binding.rcResults

        chosenCity = intent.getStringExtra("city").toString()
        binding.txtChooseDirections.text = "$chosenCity  -  Caronas Disponíveis"

        initToolBarFragment()

        putOneWayRides()


        binding.txtOption1.setOnClickListener{
            putOneWayRides()
        }

        binding.txtOption2.setOnClickListener{
            putReturnRides()
        }
    }

    override fun onResume() {
        super.onResume()
        Util.checkUserLoggedIn(this)
    }


    fun putOneWayRides() {
        resultsRC.removeAllViews()

        val ridesList: MutableMap<String, RideModel> = mutableMapOf()

        val queryCurrentUser = db.collection("Users").whereEqualTo("email", auth.currentUser!!.email)

        val queryOneWay = db.collection("Rides")
            .whereEqualTo("city", chosenCity)
            .whereEqualTo("direction", "Ida")


        queryCurrentUser.get().addOnSuccessListener { user ->
            if (!user.isEmpty) {
                val userDocument = user.documents[0]

                queryOneWay.get()
                    .addOnSuccessListener { documents ->
                        for(document in documents) {
                            val passengers = document.get("passengers") as? ArrayList<String> ?: arrayListOf()

                            if (!passengers.contains(userDocument.getString("registration"))) {
                                if (document.get("driverRegistration").toString() != userDocument.getString("registration")) {
                                    val ride = document.toObject(RideModel::class.java)

                                    ridesList[document.id] = ride
                                }
                            }
                        }
                        initRecyclerView(ridesList)

                    }
                    .addOnFailureListener {exception ->
                        Log.d(TAG, "deu ruim", exception)
                    }
            }
        }
        changeViewColors(binding.txtOption1)
    }

    fun putReturnRides() {
        resultsRC.removeAllViews()

        val ridesList: MutableMap<String, RideModel> = mutableMapOf()

        val queryReturn = db.collection("Rides")
            .whereEqualTo("city", chosenCity)
            .whereEqualTo("direction", "Volta")

        queryReturn.get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    if (document.getField<Int>("availableCarSeats")!! > 0) {
                        val ride = document.toObject(RideModel::class.java)

                        ridesList[document.id] = ride
                    }
                }
                initRecyclerView(ridesList)

            }.addOnFailureListener {exception ->
            Log.d(TAG, "deu ruim também", exception)
        }
        changeViewColors(binding.txtOption2)
    }

    private fun initRecyclerView(ridesList: Map<String, RideModel>) {
        resultsRC.layoutManager = LinearLayoutManager(this)
        resultsRC.setHasFixedSize(true)
        resultsRC.adapter = AdapterRide(this, ridesList)
    }

    private fun changeViewColors( txtView: TextView) {
        val linearLayout = binding.llOptions
        val currentIndex = linearLayout.indexOfChild(txtView)
        val previousTextView: TextView? = linearLayout.getChildAt(currentIndex - 1) as? TextView
        val nextTextView: TextView? = linearLayout.getChildAt(currentIndex + 1) as? TextView

        txtView.setTextColor(resources.getColor(R.color.white_smoke))

        if (txtView.text == "Ida") {
            if (nextTextView != null) {
                txtView.setBackgroundResource(R.drawable.border_directions_left_selected)
                txtView.setTextColor(resources.getColor(R.color.white_smoke))
                nextTextView.setBackgroundResource(R.drawable.border_directions_right)
                nextTextView.setTextColor(resources.getColor(R.color.black))
            }
        } else {
            if (previousTextView != null) {
                txtView.setBackgroundResource(R.drawable.border_directions_right_selected)
                txtView.setTextColor(resources.getColor(R.color.white_smoke))
                previousTextView.setBackgroundResource(R.drawable.border_directions_left)
                previousTextView.setTextColor(resources.getColor(R.color.black))
            }
        }
    }

    private fun initToolBarFragment() {
        val fragmentManager = (this as AppCompatActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val toolbarFragment = ToolBarFragment()

        fragmentTransaction.replace(R.id.fragment_toolbar, toolbarFragment)
        fragmentTransaction.commit()
    }
}

