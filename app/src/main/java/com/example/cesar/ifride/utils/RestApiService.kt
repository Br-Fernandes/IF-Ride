package com.example.cesar.ifride.utils

import com.example.cesar.ifride.entities.UserInfo
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class RestApiService {
    fun addUser(userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.registerEmail(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    print("\n\n\n " + t.message)
                    onResult(null)
                }
                override fun onResponse( call: Call<UserInfo>, response: Response<UserInfo>) {
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }

    fun deleteUser(userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.deleteEmail(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    print("\n\n\n " + t.message)
                    print("email falhou")
                    onResult(null)
                }
                override fun onResponse( call: Call<UserInfo>, response: Response<UserInfo>) {
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }
}