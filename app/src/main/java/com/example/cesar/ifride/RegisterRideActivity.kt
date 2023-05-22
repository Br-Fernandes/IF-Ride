package com.example.cesar.ifride

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.cesar.ifride.databinding.ActivityRegisterRideBinding
import com.example.cesar.ifride.models.RideModel
import com.example.cesar.ifride.utils.MoneyTextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class RegisterRideActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private lateinit var binding:  ActivityRegisterRideBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        configureInputs()

        binding.btnRegisterRide.setOnClickListener {
            createNewRide()
        }
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
                Log.d("TAG", "Deu bão")
            }
            .addOnFailureListener { e ->
                Log.d("TAG", "Deu caraionmemo agora", e)
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
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

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
    }
}