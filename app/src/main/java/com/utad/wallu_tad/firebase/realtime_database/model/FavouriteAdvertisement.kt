package com.utad.wallu_tad.firebase.realtime_database.model

data class FavouriteAdvertisement(
    // La key será necesaria para guardar más tarde la que reciba
    // de la base de datos. De momento será nula
    var key: String? = null,
    val userId: String = "",
    val advertisementId: String = "",
    val title: String = ""
){

}








