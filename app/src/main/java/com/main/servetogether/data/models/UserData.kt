package com.main.servetogether.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String,
    val name:String,
    val password : String,
    val isLoggedin : Boolean ,
)