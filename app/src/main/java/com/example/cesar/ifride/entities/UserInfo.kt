package com.example.cesar.ifride.entities

import com.google.gson.annotations.SerializedName

data class UserInfo (
    @SerializedName("name") val userName: String?,
    @SerializedName("login") val userEmail: String?,
    @SerializedName("email") val userAge: String?,
)