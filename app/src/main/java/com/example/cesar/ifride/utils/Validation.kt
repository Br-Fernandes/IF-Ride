package com.example.cesar.ifride.utils

class Validation {

    fun registrationValidation(registration: String): Boolean {
        return registration.isNotEmpty() && registration.all {it.isDigit()} && registration.length == 16
    }

    fun nameValidation(name: String): Boolean {
        return name.isNotEmpty() && name.all { it.isLetter() }
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

}