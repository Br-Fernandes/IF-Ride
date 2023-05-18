package com.example.cesar.ifride

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cesar.ifride.databinding.ActivityRegisterDriverBinding
import com.example.cesar.ifride.models.DriverModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterDriverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterDriverBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        auth = Firebase.auth

        binding.btnSubmitRegisterDriver.setOnClickListener {
            registerDriver()
        }
    }

   private fun registerDriver(){
       val newDriver = DriverModel(
           userReference = getuserReference(),
           cnh = binding.etCnh.text.toString().trim(),
           carModel = binding.etCarModel.text.toString().trim(),
           carColor = binding.etCarColor.text.toString().trim(),
           plate = binding.etPlate.text.toString().trim()
       )

       db.collection("Drivers")
           .add(newDriver)
           .addOnSuccessListener {
               updateIsDriver(getuserReference())
               Log.d("TAG", "Document added successfully")
           }
           .addOnFailureListener { e ->
               Log.d("TAG", "Document addition failed", e)
           }

   }

   private fun getuserReference(): DocumentReference? {
       val currentUser = auth.currentUser
       val uid = currentUser?.uid
       val userDocumentRef = uid?.let { db.collection("Users").document(it) }

       return userDocumentRef
   }

   private fun updateIsDriver(user: DocumentReference?) {
       if (user != null) {
           user.update("isDriver", true)
               .addOnSuccessListener {
                   Log.d("TAG", "Element updated successfully")
               }
               .addOnFailureListener { e ->
                   Log.d("TAG", "Element update failed", e)
               }
       }
   }
}