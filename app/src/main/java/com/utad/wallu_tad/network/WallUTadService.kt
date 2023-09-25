package com.utad.wallu_tad.network

import com.utad.wallu_tad.network.model.body.AdvertisementBody
import com.utad.wallu_tad.network.model.responses.Advertisement
import com.utad.wallu_tad.network.model.body.CredentialsBody
import com.utad.wallu_tad.network.model.responses.BasicResponse
import com.utad.wallu_tad.network.model.responses.SaveUserResponse
import com.utad.wallu_tad.network.model.responses.TokenResponse
import com.utad.wallu_tad.network.model.body.UserBody
import com.utad.wallu_tad.network.model.responses.UserDataResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WallUTadService {
    //region --- User ---

    @POST("user")
    fun saveUser(@Body user: UserBody): Call<SaveUserResponse>

    @GET("user")
    suspend fun getUserData(@Header("Authorization") token: String): Response<UserDataResponse>

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
    suspend fun createAdvertisement(
        @Header("Authorization") token: String,
        @Body advertisementBody: AdvertisementBody
    ): Response<BasicResponse>

    //endregion --- Advertisement ---
}