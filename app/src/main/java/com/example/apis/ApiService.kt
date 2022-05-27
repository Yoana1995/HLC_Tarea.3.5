package com.example.apis

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/0.2/languages")
    suspend fun getLanguages(): Response<List<Language>>
    @FormUrlEncoded
    @Headers("Authorization: Bearer 68078b51b76e53687de408d421ad80c4")
    @POST("/0.2/detect")
    suspend fun getTextLanguage(@Field("q")text: String):Response<DetectionResponse>
}