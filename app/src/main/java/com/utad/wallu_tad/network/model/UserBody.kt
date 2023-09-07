package com.utad.wallu_tad.network.model

import com.google.gson.annotations.SerializedName

data class UserBody(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)
