package com.example.cesar.ifride

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cesar.ifride.databinding.ActivityRegisterDriverBinding
import com.example.cesar.ifride.models.DriverModel
import com.example.cesar.ifride.utils.Validation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

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

        MainActivity.getInstance()!!.verifyAuthetication()

        binding.btnSubmitRegisterDriver.setOnClickListener {
            registerDriver()
        }

        initToolBarFragment()

    }

    private fun registerDriver() {
        if (validationInputs()) {
            val query = db.collection("Drivers")
                .whereEqualTo("cnh", binding.etCnh.text.toString().trim())
                .whereEqualTo("plate", binding.etPlate.text.toString().trim())

            query.get().addOnSuccessListener { querySnapshot ->
                if (querySnapshot.size() == 0) {
                    getUserQuerySnapshot().addOnSuccessListener { querySnapshot2 ->
                        val document = querySnapshot2.documents[0]
                        val userReference = document.getString("registration").toString()

                        val newDriver = DriverModel(
                            userReference = userReference,
                            cnh = binding.etCnh.text.toString().trim(),
                            carModel = binding.etCarModel.text.toString().trim(),
                            carColor = binding.etCarColor.text.toString().trim(),
                            plate = binding.etPlate.text.toString().uppercase().trim()
                        )

                        db.collection("Drivers")
                            .add(newDriver)
                            .addOnSuccessListener {
                                Log.d("TAG", userReference)
                                updateIsDriver(userReference)
                                Log.d("TAG", "Document added successfully")
                                openRegisterRide()
                            }
                            .addOnFailureListener { e ->
                                Log.d("TAG", "Document addition failed", e)
                            }
                    }
                } else {
                    Log.d("TAG", "Dados já cadastrados")
                }
            }
        } else {
            Log.d("TAG","Inputs inválidos" )
        }
    }

    private fun updateIsDriver(userRegistration: String?) {
        val query = db.collection("Users").whereEqualTo("registration", userRegistration)

        query.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val user = documents.documents[0].id

                db.collection("Users")
                    .document(user)
                    .update("isDriver", true)
            }
        }
    }

    private fun getUserQuerySnapshot(): Task<QuerySnapshot> {
        val currentUser = auth.currentUser
        val email = currentUser?.email

        val query = db.collection("Users").whereEqualTo("email", email)

        return query.get().addOnSuccessListener {
                    Log.d("TAG", "Success retrieving user reference")
                }.addOnFailureListener { e ->
                    Log.d("TAG", "Error retrieving user reference", e)
                }
    }

    private fun validationInputs(): Boolean {
        val vali = Validation()

        return vali.cnhValidation(binding.etCnh.text.toString().trim()) &&
               vali.carModelValidation(binding.etCarModel.text.toString().trim()) &&
               vali.carColorValidation(binding.etCarColor.text.toString().trim()) &&
               vali.plateValidation(binding.etPlate.text.toString().trim())
    }

    private fun openRegisterRide() {
        val intent = Intent(this, RegisterRideActivity::class.java)
        startActivity(intent)
    }

    private fun initToolBarFragment() {
        val fragmentManager = (this as AppCompatActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val toolbarFragment = ToolBarFragment()

        fragmentTransaction.replace(R.id.fragment_toolbar, toolbarFragment)
        fragmentTransaction.commit()
    }
}