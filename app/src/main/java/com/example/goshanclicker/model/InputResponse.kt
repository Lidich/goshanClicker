package com.example.goshanclicker.model

import com.google.gson.annotations.SerializedName

data class InputResponse(
    @SerializedName("status")
    val status: Int
)