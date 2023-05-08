package com.example.cesar.ifride.models

import java.util.Date

open class RideModel (
    val driverName: String,
    val dateHour: Date,
    val city: String,
    //val passengers: MutableList<UserModel> = mutableListOf(),
    val price: String,
    val direction: String,
    val availableCarSeats: Int
) {
    constructor() : this("", Date(), "", /*mutableListOf(),*/ "", "", 0)
}
