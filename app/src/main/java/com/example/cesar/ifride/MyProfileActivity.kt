package com.example.cesar.ifride

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cesar.ifride.databinding.ActivityMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        configureMyProfile()
    }

    fun configureMyProfile() {
        val txtFullName = binding.txtFullnameProfile

        var registrationValue = binding.registrationValue
        var emailValue = binding.emailValue
        var phoneValue = binding.phoneValue

        var carModelValue = binding.carModelValue
        var carColorValue = binding.carColorValue
        var plate = binding.plateValue

        val queryCurrentUser = db.collection("Users").whereEqualTo("email", auth.currentUser!!.email)
        queryCurrentUser.get().addOnSuccessListener { querySnapshot ->
            val documentUser = querySnapshot.documents[0]

            txtFullName.text = documentUser.getString("name")

            registrationValue.text = documentUser.getString("registration")
            emailValue.text = documentUser.getString("email")
            phoneValue.text = documentUser.getString("phone")

            if (documentUser.getBoolean("isDriver") == true) {
                binding.rlDriver.visibility = View.VISIBLE

                val queryCurrentDriver = db.collection("Drivers").whereEqualTo("userReference", documentUser.getString("registration"))
                queryCurrentDriver.get().addOnSuccessListener { querySnapshot2 ->
                    val documentDriver = querySnapshot2.documents[0]

                    carModelValue.text = documentDriver.getString("carModel")
                    carColorValue.text = documentDriver.getString("carColor")
                    plate.text = documentDriver.getString("plate")
                }

            }


        }

    }
}