package com.example.cesar.ifride.models

import com.google.firebase.firestore.DocumentReference

open class RideModel(
    val direction: String = "",
    val driverRegistration: String? = "",
    val dateHour: String = "",
    val city: String = "",
    val price: String = "",
    val availableCarSeats: Int = 0,
    val passengers: MutableList<String> = mutableListOf()
) {
    constructor() : this("", "", "", "", "", 0)
}

