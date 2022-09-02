package com.example.quiz_time.RetroFitHelperPackage

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroFitInstance {
    val apiInstance: AnswerApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://b4e7d359-c58f-4aa3-a314-726b3baa3852.mock.pstmn.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnswerApi::class.java)
    }
}