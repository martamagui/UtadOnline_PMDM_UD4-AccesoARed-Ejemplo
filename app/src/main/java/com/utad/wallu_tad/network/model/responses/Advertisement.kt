package com.utad.wallu_tad.network.model.responses

import com.google.gson.annotations.SerializedName

data class Advertisement(
    @SerializedName("_id")
    val id: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("title")
    val title: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("tags")
    val tags: List<String>?
)