package com.example.quiz_time.DataClasses

import android.os.Parcel
import android.os.Parcelable


data class Question(
    val correct_answers: List<Int>,
    val lable: String,
    val options: List<Option>
)