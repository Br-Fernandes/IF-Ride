package com.example.cesar.ifride

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.LinearLayout
import android.widget.TextView
import com.example.cesar.ifride.databinding.ActivityMainBinding
import com.example.cesar.ifride.utils.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        instance = this

        verifyAuthetication()

        configureBottomNavigation()

        chooseCities()
    }

    private fun chooseCities() {
        var relativeLayout = binding.rlCities

        for (i in 0 until relativeLayout.childCount) {
            val linearLayout = relativeLayout.getChildAt(i) as LinearLayout
            for (j in 0 until linearLayout.childCount) {
                val textView = linearLayout.getChildAt(j) as TextView
                textView.setOnClickListener {
                    openRidesActivity(textView.text.toString())
                }
            }
        }
    }

    private fun openRidesActivity(city: String) {
        var intent = Intent(this, RidesActivity::class.java)
        intent.putExtra("city", city)
        startActivity(intent)
    }

    private fun configureBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.cities

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cities -> {
                    true
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
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                    finish()
                }
                else -> Unit
            }
            false
        }

    }

    companion object {
        private var instance: MainActivity?  = null

        fun getInstance(): MainActivity? {
            return instance
        }
    }

    fun verifyAuthetication() {
        if (Util.verifyCurrentUser()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}