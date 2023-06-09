package com.example.cesar.ifride.entities


import com.google.gson.annotations.SerializedName

data class UserInfo (
    @SerializedName("login") val userLogin: String,
    @SerializedName("name") val userName: String?,
    @SerializedName("email") val userEmail: String?,
    @SerializedName("phone") val userPhone: String?
)