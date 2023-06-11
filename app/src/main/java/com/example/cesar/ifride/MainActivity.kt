package com.example.cesar.ifride

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.cesar.ifride.databinding.ActivityMainBinding
import com.example.cesar.ifride.utils.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        instance = this
        drawerLayout = findViewById(R.id.container_drawer)

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
                R.id.cities -> true
                R.id.rides -> {
                    Util.verifyIsDriver { isDriver ->
                        if (isDriver) {
                            val intent = Intent(this, RegisterRideActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        } else {
                            val intent = Intent(this, RegisterDriverActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    true
                }
                R.id.mine_rides -> {
                    val intent = Intent(this, MineRidesActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.account -> {
                    AccountSideBar.configureSideBar(drawerLayout)
                    true
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