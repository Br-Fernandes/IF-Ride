package com.example.cesar.ifride.models

import com.google.firebase.firestore.DocumentReference

open class DriverModel(
    val userReference: DocumentReference,
    val cnh: String,
    val carModel: String,
    val carColor: String,
    val plate: String
) {
}