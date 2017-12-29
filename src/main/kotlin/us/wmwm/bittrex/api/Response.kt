package us.wmwm.bittrex.api

import com.google.gson.annotations.SerializedName

abstract class Response {
    @SerializedName("success")
    var success= false
    @SerializedName("message")
    var message:String = ""
}