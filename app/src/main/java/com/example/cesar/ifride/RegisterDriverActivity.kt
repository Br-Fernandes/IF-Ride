package com.example.cesar.ifride

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cesar.ifride.databinding.ActivityRegisterDriverBinding
import com.example.cesar.ifride.models.DriverModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterDriverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterDriverBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
    }

   private fun registerDriver(){
       
   }
}