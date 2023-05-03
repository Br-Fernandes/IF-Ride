package com.example.cesar.ifride.utils

import com.example.cesar.ifride.entities.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RestApi {

    @Headers("Content-Type: application/json")
    @POST("users")
    fun registerEmail(@Body userData: UserInfo): Call<UserInfo>

}