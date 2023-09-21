package com.utad.wallu_tad.network.model

import android.os.Message
import com.google.gson.annotations.SerializedName

data class SaveUserResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("token")
    val token: String
)
