package com.javilena87.fichaje.data.remote

import com.javilena87.fichaje.data.model.SigningData
import com.javilena87.fichaje.data.model.UserData
import retrofit2.http.*

interface FichajeApi {

    @FormUrlEncoded
    @POST("/my/api/login")
    suspend fun login(@Field("UserName") userName: String, @Field("Password") password: String): UserData

    @GET("/my/api/signing?type=checkOut")
    suspend fun exit(@Query("name") name: String): SigningData

    @GET("/my/api/signing?type=checkIn")
    suspend fun enter(@Query("name") name: String): SigningData
}