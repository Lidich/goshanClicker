package com.example.goshanclicker

import com.google.gson.annotations.SerializedName

data class InputResponse(
    @SerializedName("status")
    val status: Int
)