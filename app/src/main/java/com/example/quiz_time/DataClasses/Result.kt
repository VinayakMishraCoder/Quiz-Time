package com.example.quiz_time.DataClasses

data class Result(
    val questions: List<Question>,
    val timeInMinutes: Int
)