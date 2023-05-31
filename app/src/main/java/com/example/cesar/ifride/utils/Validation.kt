package com.example.cesar.ifride.utils

import com.google.android.gms.common.api.Api
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.core.FirestoreClient
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Future

class Validation {

    val db = Firebase.firestore

    fun registrationValidation(registration: String): Boolean {
        val query = db.collection("Users").whereEqualTo("registration", registration)

        val querySnapshotTask = query.get()
        Tasks.await(querySnapshotTask) // Espera até que a tarefa seja concluída

        val querySnapshot = querySnapshotTask.result

        if (querySnapshot.size() == 0) {
            return registration.isNotEmpty() && registration.all { it.isDigit() } && registration.length == 16
        }
        return false
    }


    fun nameValidation(name: String): Boolean {
        return name.isNotEmpty() && name.all { it.isLetter() || it.isWhitespace() }
    }


    fun emailValidation(email: String): Boolean {
        val emailRegex = Regex("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9\\-\\.]+)\\.([a-zA-Z]{2,5})\$")
        return email.isNotEmpty() && email.matches(emailRegex)
    }

    fun phoneValidation(phone: String): Boolean {
        return phone.isNotEmpty() && phone.all {it.isDigit()} && phone.length == 11
    }

    fun passwordValidation(password: String, password2: String): Boolean {
        return password.isNotEmpty() && password2.isNotEmpty() && password == password2
    }

    fun cnhValidation(cnh: String): Boolean {
        return cnh.isNotEmpty() && cnh.all {it.isDigit()} && cnh.length == 9
    }

    fun carModelValidation(carModel: String): Boolean {
        return carModel.isNotEmpty()
    }

    fun carColorValidation(carColor: String): Boolean {
        return carColor.isNotEmpty() && carColor.all {it.isLetter()}
    }

    fun plateValidation(plate: String): Boolean {
        val standard =  Regex("[A-Z]{3}[0-9]{4}")
        return plate.isNotEmpty() && plate.matches(standard)
    }

}