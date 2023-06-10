package com.example.cesar.ifride

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.cesar.ifride.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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

        Log.d("TAG", "asdfoisdafsioadf2222222\n\n\n\n\n\n\n\n\n")


        binding.txtRegisterActivity.setOnClickListener {
            openRegisterActivity()
        }


        binding.btnSubmitLogin.setOnClickListener {
            Log.d("TAG", "asdfoisdafsioadf\n\n\n\n\n\n\n\n\n")
            register()
        }
    }

    private fun register() {
        val registration = binding.etRegistration.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        Log.d("TAG", "$registration")

        val userRef = db.collection("Users").document(registration)

        userRef.get()
            .addOnSuccessListener { document ->
                val email = document.getString("email")

                loginEmailPassword(email, password)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Deu ruim", e)
            }

        if (!userRef.get().isSuccessful)
            binding.loginErrorMessage.setText(R.string.error_message)

    }

    private fun loginEmailPassword(email: String?, password: String) {
        if (email != null) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        val exception = task.exception
                    }
                }
        }
    }

    private fun openRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}