package com.utad.wallu_tad.network.model

import com.google.gson.annotations.SerializedName

data class BasicResponse(
    @SerializedName("message")
    val message: String
)
