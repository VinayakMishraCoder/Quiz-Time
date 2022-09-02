package com.example.quiz_time.DataClasses

import android.net.Uri

data class User(
    val username:String = "Quiz Master",
    val uri: String = "NOT_SET",
    val highestScore: Int = 0
)