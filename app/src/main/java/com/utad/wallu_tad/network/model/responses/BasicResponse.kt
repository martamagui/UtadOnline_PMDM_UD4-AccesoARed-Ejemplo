package com.utad.wallu_tad.network.model.responses

import com.google.gson.annotations.SerializedName

data class BasicResponse(
    @SerializedName("message")
    val message: String
)
