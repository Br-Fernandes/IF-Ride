package com.example.cesar.ifride.models

import com.google.firebase.firestore.DocumentReference
import java.util.Date

open class RideModel(
    val direction: String = "",
    val driverName: String = "",
    val dateHour: Date = Date(),
    val city: String = "",
    val price: String = "",
    val availableCarSeats: Int = 0,
    val passengers: MutableList<DocumentReference> = mutableListOf()
) {
    constructor() : this("", "", Date(), "", "", 0)
}

