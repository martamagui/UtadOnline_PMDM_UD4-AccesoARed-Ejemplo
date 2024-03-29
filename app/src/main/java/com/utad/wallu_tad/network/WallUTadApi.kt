package com.utad.wallu_tad.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WallUTadApi {
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://ud4-server.onrender.com/api/v1/")//El url base siempre debe acabar en /
        .client(client)//Intercepta por consola los datos enviados y recibidos de las peticiones
        .addConverterFactory(GsonConverterFactory.create())//Parsea el json recibido a nuestras data class
        .build()

    val service: WallUTadService by lazy {
        retrofit.create(WallUTadService::class.java)
    }
}