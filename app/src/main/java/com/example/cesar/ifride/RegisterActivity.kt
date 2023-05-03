package com.example.cesar.ifride

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cesar.ifride.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.cesar.ifride.utils.Validation as Validation


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore

        binding.btnSubmitRegister.setOnClickListener {
            Log.d(TAG, "sjkdhfjksadfjsahfjksahkjfjkshfjksahfjksajkf ABROBRA")
            register(db)
        }

        binding.txtLoginActivity.setOnClickListener {
            openLoginActivity()
        }
    }


    fun register(db: FirebaseFirestore) {
        val registrationNumber = binding.etRegistration.text.toString().trim()
        val newUser = hashMapOf(
            "name" to binding.etName.text.toString().trim(),
            "email" to binding.etEmail.text.toString().trim(),
            "phone" to binding.etPhone.text.toString().trim(),
            "password" to binding.etPassword.text.toString().trim(),
            "isDriver" to false
        )

        if (validatingInputs()) {
            db.collection("Users")
                .document(registrationNumber)
                .set(newUser)
                .addOnSuccessListener {
                    Log.d(TAG, "The document was add successfully added")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        } else {
            Log.d(TAG, "deu errado!!")
        }
    }

    fun validatingInputs(): Boolean {
        val validation = Validation()

        return  validation.registrationValidation(binding.etRegistration.text.toString().trim()) &&
                validation.nameValidation(binding.etName.text.toString().trim()) &&
                validation.emailValidation(binding.etEmail.text.toString().trim()) &&
                validation.phoneValidation(binding.etPhone.text.toString().trim()) &&
                validation.passwordValidation(binding.etPassword.text.toString().trim(), binding.etRepeatPassword.text.toString().trim())
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}