package com.utad.wallu_tad.db.model

data class FavouriteAdvertisement(
    var key: String? = null,
    val userId: String,
    val advertisementId: String,
    val title: String
)
