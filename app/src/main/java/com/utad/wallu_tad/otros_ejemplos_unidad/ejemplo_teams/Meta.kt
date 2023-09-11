package com.utad.wallu_tad.otros_ejemplos_unidad.ejemplo_teams

import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("next_page")
    val nextPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total_count")
    val totalCount: Int
)












