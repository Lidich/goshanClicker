package com.example.goshanclicker.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class InputRequest(
    @SerializedName("image")
    val image: String,

    @SerializedName("ms_since_click")
    val msSinceClick: Int? = null,

    @SerializedName("timestamp")
    val timestamp: Long? = null,

    val bitmap: Bitmap? = null
)