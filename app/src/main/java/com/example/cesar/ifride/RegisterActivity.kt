package com.example.cesar.ifride

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cesar.ifride.databinding.ActivityRegisterBinding
import com.example.cesar.ifride.entities.UserInfo
import com.example.cesar.ifride.models.UserModel
import com.example.cesar.ifride.utils.RestApiService
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.cesar.ifride.utils.Validation as Validation


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore
        auth = Firebase.auth

        binding.btnSubmitRegister.setOnClickListener {
            register(db)
        }

        binding.txtLoginActivity.setOnClickListener {
            openLoginActivity()
        }
    }


    fun register(db: FirebaseFirestore) {
        val registrationNumber = binding.etRegistration.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val newUser = UserModel(
            registration = registrationNumber,
            name  = binding.etName.text.toString().trim(),
            email = email,
            phone= binding.etPhone.text.toString().trim(),
            isDriver = false
        )

        val userInfo = UserInfo(
            userName = binding.etName.text.toString().trim(),
            userEmail = email,
            userPhone = binding.etPhone.text.toString().trim()
        )

        val apiService = RestApiService()

        if (validatingInputs()) {
            val query = db.collection("Users")
                .whereEqualTo("email", email)
                .whereEqualTo("registration", registrationNumber)

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot.isEmpty) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    db.collection("Users")
                                        .document(registrationNumber)
                                        .set(newUser)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                openLoginActivity()
                                                Log.d(TAG, "O documento foi adicionado com sucesso")
                                            } else {
                                                Log.w(TAG, "Erro ao adicionar documento", dbTask.exception)
                                            }
                                        }
                                } else {
                                    Log.w(TAG, "Erro ao criar registro de autenticação", authTask.exception)
                                }
                            }
                    } else {
                        Log.d(TAG, "Dados já cadastrados")
                    }
                } else {
                    Log.w(TAG, "Erro ao executar a consulta", task.exception)
                }
            }
        } else {
            Log.d(TAG, "Dados inválidos!")
        }


        apiService.addUser(userInfo) {
            if (it?.userEmail != null) {
                print("\n\n\n\nSuccess registering new user")
            } else {
                print("\n\n\n\nError registering new user")
            }
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
        finish()
    }
}