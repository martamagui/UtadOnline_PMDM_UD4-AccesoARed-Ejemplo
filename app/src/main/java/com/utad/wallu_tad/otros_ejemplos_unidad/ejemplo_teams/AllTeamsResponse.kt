package com.utad.wallu_tad.otros_ejemplos_unidad.ejemplo_teams

import com.google.gson.annotations.SerializedName
import com.utad.wallu_tad.otros_ejemplos_unidad.ejemplo_team_11.Team

data class AllTeamsResponse(
    //Cuando recibamos un array o una lista
    // de objetos, lo declararemos así
    @SerializedName("data")
    val data: List<Team>,

    //Esto sería un ejemplo de recibir
    // un objeto dentro de otro
    @SerializedName("meta")
    val meta: Meta
)




























