package com.example.cesar.ifride

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.cesar.ifride.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        auth = Firebase.auth

        binding.txtRegisterActivity.setOnClickListener {
            openRegisterActivity()
        }


        binding.btnSubmitLogin.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val registration = binding.etRegistration.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val userRef: DocumentReference
        Log.d("TAG", registration)

        if(registration != "")
            userRef = db.collection("Users").document(registration)
        else{
            binding.loginErrorMessage.setText(R.string.error_message)
            return
        }


        userRef.get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    val email = document.getString("email")
                    loginEmailPassword(email, password)
                } else {
                    binding.loginErrorMessage.setText(R.string.error_message)
                }

            }
    }

    private fun loginEmailPassword(email: String?, password: String) {
        if (email != null) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        binding.loginErrorMessage.setText(R.string.error_message)
                    }
                }
        }
    }

    private fun openRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}