package com.utad.wallu_tad.network

import com.utad.wallu_tad.network.model.Advertisement
import com.utad.wallu_tad.network.model.CredentialsBody
import com.utad.wallu_tad.network.model.BasicResponse
import com.utad.wallu_tad.network.model.TokenResponse
import com.utad.wallu_tad.network.model.UserBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WallUTadService {
    //region --- User ---
    @GET("user/is-username-taken/{userName}")
    fun checkIfUserNameIsTaken(@Path("userName") userName: String): Call<BasicResponse>

    @POST("user")
    fun saveUser(@Body user: UserBody): Call<BasicResponse>

    @POST("user/login")
    fun login(@Body credentialsBody: CredentialsBody): Call<TokenResponse>

    //endregion --- User ---


    //region --- Advertisement ---


    @GET("advertisement/{id}")
    fun getAdvertisementId(@Path("id") id: String): Call<Advertisement>

    //endregion --- Advertisement ---
}