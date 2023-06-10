package com.example.cesar.ifride

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.cesar.ifride.databinding.ActivityAccountBinding
import com.example.cesar.ifride.entities.UserInfo
import com.example.cesar.ifride.utils.RestApiService
import com.example.cesar.ifride.utils.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAccountBinding
    private var emailService = RestApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        MainActivity.getInstance()!!.verifyAuthetication()

        setUserNameOnTitle()

        logout()
        deleteAccount()

        configureBottomNavigation()
    }

    private fun setUserNameOnTitle() {
        val accountTitle = binding.tvTitleRegister
        val currentUserEmail = auth.currentUser!!.email

        val query = db.collection("Users").whereEqualTo("email", currentUserEmail)
        query.get().addOnSuccessListener { querySnapshot ->
            val currentUserName = querySnapshot.documents[0].get("name").toString()
            val (firstName) = currentUserName.split(" ")
            accountTitle.text = "Olá, $firstName"
        }
    }

    private fun logout() {
        binding.logoutOption.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteAccount() {
        binding.deleteAccountOption.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage(R.string.alert_dialog_message)
            alertDialogBuilder.setPositiveButton("Sim") { dialog, which ->
                val currentUser = auth.currentUser

                val query = db.collection("Users").whereEqualTo("email", currentUser!!.email)
                query.get().addOnSuccessListener { querySnapshot ->

                    val document = querySnapshot.documents[0]
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val phone = document.getString("phone")
                    val userInfo = UserInfo(document.id, name, email, phone)

                    querySnapshot.documents[0].reference.delete().addOnSuccessListener {
                        currentUser.delete()

                        emailService.deleteUser(userInfo) {
                            if (it?.userEmail != null) {
                                print("\n\n\n\nSuccess deleting user")
                            } else {
                                print("\n\n\n\nError deleting user")
                            }
                        }

                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            alertDialogBuilder.setNegativeButton("Não") {dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun configureBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.account

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cities -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                    finish()
                }
                R.id.rides -> {
                    Util.verifyIsDriver { isDriver ->
                        if (isDriver) {
                            val intent = Intent(this, RegisterRideActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            true
                            finish()
                        } else {
                            val intent = Intent(this, RegisterDriverActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
                R.id.mine_rides -> {
                    val intent = Intent(this, MineRidesActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                    finish()
                }
                R.id.account -> {
                    true
                }
                else -> Unit
            }
            false
        }
    }
}