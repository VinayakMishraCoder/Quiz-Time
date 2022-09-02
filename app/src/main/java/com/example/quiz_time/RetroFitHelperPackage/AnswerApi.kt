package com.example.quiz_time.RetroFitHelperPackage

import com.example.quiz_time.DataClasses.AnswerSet
import retrofit2.Response
import retrofit2.http.GET

interface AnswerApi {
    @GET("/?quiz=true")
    suspend fun getTodos(): Response<AnswerSet>
}