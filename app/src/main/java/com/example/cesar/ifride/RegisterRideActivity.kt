package com.example.cesar.ifride

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import com.example.cesar.ifride.databinding.ActivityRegisterRideBinding
import com.example.cesar.ifride.models.RideModel
import com.example.cesar.ifride.utils.MoneyTextWatcher
import com.example.cesar.ifride.utils.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

@Suppress("UNUSED_EXPRESSION")
class RegisterRideActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private lateinit var binding:  ActivityRegisterRideBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.container_drawer)

        configureBottomNavigation()

        configureInputs()

        binding.btnRegisterRide.setOnClickListener {
            createNewRide()
        }
    }

    override fun onResume() {
        super.onResume()
        Util.checkUserLoggedIn(this)
    }


    private fun createNewRide() {
        val city = binding.spinnerCities.selectedItem.toString()
        val direction = binding.spinnerDirection.selectedItem.toString()
        val price = binding.etPrice.text.toString()
        val availableCarSeats = binding.etCarSeats.text.toString().toInt()
        val dateHour = "$savedDay-$savedMonth-$savedYear-$savedHour-$savedMinute"

        if (city != "Cidades" && direction != "Direção"){
            val currentUserEmail = auth.currentUser!!.email

            val query = db.collection("Users").whereEqualTo("email", currentUserEmail)

            query.get().addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents[0]
                val driverRegistration = document.getString("registration")

                registerNewRide(driverRegistration, city, direction, price, availableCarSeats, dateHour)
            }
        }
    }

    private fun registerNewRide(driverRegistration: String?, city: String, direction: String, price: String, availableCarSeats: Int, dateHour: String) {
        val newRide = RideModel(
            driverRegistration = driverRegistration,
            city = city,
            direction = direction,
            price = price,
            dateHour = dateHour,
            availableCarSeats = availableCarSeats
        )

        db.collection("Rides").add(newRide)
            .addOnSuccessListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                Log.d("TAG", "Adicionado com sucesso")
            }
            .addOnFailureListener { e ->
                Log.d("TAG", "Algo deu errado", e)
            }
    }


    private fun configureInputs() {
        configureCitiesInput()
        configureDirectionInput()

        val priceInput = binding.etPrice
        priceInput.addTextChangedListener(MoneyTextWatcher(priceInput))

        pickDate()
    }

    private fun configureCitiesInput() {
        val spinner = binding.spinnerCities

        ArrayAdapter.createFromResource(
            this,
            R.array.cities_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.layout_dropdown)
            spinner.adapter = adapter
        }
    }

    private fun configureDirectionInput() {
        val spinner = binding.spinnerDirection

        ArrayAdapter.createFromResource(
            this,
            R.array.direction_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.layout_dropdown)
            spinner.adapter = adapter
        }
    }

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun pickDate() {
        binding.btnDateHour.setOnClickListener {
            getDateTimeCalendar()

            DatePickerDialog(this, this, year, month, day).show()
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(this, this, hour, minute, true).show()
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        binding.txtSavedDateHour.text = Util.formatDate("$savedDay-$savedMonth-$savedYear-$savedHour-$savedMinute")
        binding.txtSavedDateHour.visibility = View.VISIBLE
    }

    private fun configureBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.rides

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cities -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    true
                    finish()
                }
                R.id.rides -> {
                    true
                }
                R.id.mine_rides -> {
                    val intent = Intent(this, MineRidesActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                    finish()
                }
                R.id.account -> {
                    AccountSideBar.configureSideBar(this,drawerLayout)
                    true
                }
                else -> Unit
            }
            false
        }

    }
}