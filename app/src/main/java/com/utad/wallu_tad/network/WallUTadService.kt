package com.utad.wallu_tad.network

import com.utad.wallu_tad.network.model.Advertisement
import com.utad.wallu_tad.network.model.CredentialsBody
import com.utad.wallu_tad.network.model.BasicResponse
import com.utad.wallu_tad.network.model.TokenResponse
import com.utad.wallu_tad.network.model.UserBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WallUTadService {

    @POST("user")
    fun saveUser(@Body user: UserBody): Call<BasicResponse>

    @POST("user/login")
    fun login(@Body credentialsBody: CredentialsBody): Call<TokenResponse>

    @GET("user/is-username-taken/{userName}")
    fun checkIfUserNameIsTaken(@Path("userName") userName: String): Call<BasicResponse>

    //endregion --- User ---


    //region --- Advertisement ---
    @GET("advertisement")
    suspend fun getAllAdvertisements(): Response<List<Advertisement>>

    @GET("advertisement/{id}")
    fun getAdvertisementId(@Path("id") id: String): Call<Advertisement>

    @POST("create-advertisement")
    fun createAdvertisement(
        @Header("Authorization") token: String,
        @Body credentialsBody: CredentialsBody
    ): Call<BasicResponse>

    //endregion --- Advertisement ---
}