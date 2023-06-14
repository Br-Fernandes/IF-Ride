package com.example.cesar.ifride

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.LinearLayout
import android.widget.TextView

import androidx.drawerlayout.widget.DrawerLayout
import com.example.cesar.ifride.databinding.ActivityMainBinding
import com.example.cesar.ifride.utils.Util
import com.example.cesar.ifride.utils.Util.Companion.checkUserLoggedIn
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
        drawerLayout = findViewById(R.id.container_drawer)

        configureBottomNavigation()

        chooseCities()
    }

    override fun onResume() {
        super.onResume()
        checkUserLoggedIn(this)
    }

    private fun chooseCities() {
        val relativeLayout = binding.rlCities

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
        val intent = Intent(this, RidesActivity::class.java)
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
                            startActivity(Intent(this, RegisterRideActivity::class.java))
                            overridePendingTransition(0, 0)
                            finish()
                        } else {
                            startActivity(Intent(this, RegisterDriverActivity::class.java))
                        }
                    }
                    true
                }
                R.id.mine_rides -> {
                    startActivity(Intent(this, MineRidesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.account -> {
                    AccountSideBar.configureSideBar(this, drawerLayout)
                    true
                }
                else -> Unit
            }
            false
        }
    }
}